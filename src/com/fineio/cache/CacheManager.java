package com.fineio.cache;


import com.fineio.base.SingleWaitThread;
import com.fineio.base.Worker;
import com.fineio.io.file.FileBlock;
import com.fineio.io.Buffer;
import com.fineio.io.base.BufferCreator;
import com.fineio.memory.MemoryConf;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by daniel on 2017/3/1.
 * 内存的总量根据设置值来
 * 写内存和读内存的占比根据访问的情况来控制
 * 如果没有读的进程，那么全力输出写
 * 如果没有写的进程，那么全力输出读
 * 如果写或者读的等待内容发生了不平衡read_wait_count 与write_wait_count
 * 则控制read与wait所占的内存比例进行偏向来维持平衡
 * 由于write的内存是不能释放的当write偏多的时候则控制新write的申请，
 * 如果是write等待偏多，则适当释放read来给write提供更多的资源
 * 当资源满载的情况下write任务有等待的时候write总量不低于总内存的1/8,当没有任务的时候该10%可以被读占据
 * 这个值并非精确空值 而是在内存满负荷的情况下write的总量效率1/8的时候选择唤醒write线程
 */
public class CacheManager {

    private volatile static CacheManager instance;

    /**
     * 默认10分钟清理超时buffer
     */
    private static final long DEFAULT_TIMER_TIME = 600000;
    private static final int LEVEL_LINE = 2;
    private static final int MIN_WRITE_OFFSET = 3;
    //内存分配锁
    private Lock memoryLock = new ReentrantLock();
    //GC的线程
    private SingleWaitThread gcThread;
    //超时的timer
    private Timer timer = new Timer();
    //正在读的内存大小
    private volatile AtomicWatchLong read_size = new AtomicWatchLong();
    //正在读的等待的数量
    private volatile AtomicInteger read_wait_count = new AtomicInteger(0);
    //写的内存大小
    private volatile AtomicWatchLong write_size = new AtomicWatchLong();
    //正在写的等待的数量
    private volatile AtomicInteger write_wait_count = new AtomicInteger(0);


    private Map<FileBlock, Buffer> read = new ConcurrentHashMap<FileBlock, Buffer>();
    private Map<FileBlock, Buffer> edit = new ConcurrentHashMap<FileBlock, Buffer>();
    private Map<FileBlock, Buffer> write = new ConcurrentHashMap<FileBlock, Buffer>();

    public static CacheManager getInstance(){
        if(instance == null){
            synchronized (CacheManager.class){
                if(instance == null){
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }

    public Buffer createReadBuffer(BufferCreator creator){
        Buffer buffer = creator.create();
        return  buffer;
    }

    /**
     * 构造函数
     */
    private CacheManager(){
        timer.schedule(createTask(), DEFAULT_TIMER_TIME, DEFAULT_TIMER_TIME);
        read_size.addListener(createReadWatcher());
        write_size.addListener(createWriteWatcher());
        gcThread = new SingleWaitThread(new Worker() {
            public void work() {
                gc();
            }
        });
    }

    /**
     * 读的观察者
     * 读的线程优先唤醒读
     * @return
     */
    private Watcher createReadWatcher () {
        return new Watcher() {
            public void watch(long change) {
                if(change < 0){
                    if(checkNotifyWrite()) {
                        notifyWrite();
                    } else {
                        notifyRead();
                    }
                }
            }
        };
    }

    /**
     * 更多的唤醒工作
     * 优先唤醒读
     * 在分配内存之后的触发上执行
     */
    private void doMoreNotifyCheck() {
        if(checkNotifyWrite()) {
            notifyWrite();
        } else {
            notifyRead();
        }
    }

    /**
     * 分配读内存
     * @param size
     * @return
     */
    public long allocateRead(long size) {
        try {
            read_wait_count.addAndGet(1);
            return allocateRW(read_size, new NewAllocator(size));
        } finally {
            read_wait_count.addAndGet(-1);
        }
    }

    /**
     * 分配写内存
     * @param address 旧地址 没有为0
     * @param oldSize 旧大小 没有为0
     * @param newSize 新大小
     * @return
     */
    public long allocateWrite(long address, long oldSize, long newSize) {
        try {
            write_wait_count.addAndGet(1);
            return allocateRW(write_size, new ReAllocator(address, oldSize, newSize));
        } finally {
            write_wait_count.addAndGet(-1);
        }
    }

    /**
     * 分配内存的方法
     * @param rw 保存尺寸的对象
     * @param allocator 分配方法
     * @return
     */
    private long allocateRW(AtomicWatchLong rw, Allocator allocator) {
        memoryLock.lock();
        //判断条件加上锁把
        if(cs(allocator.getChangeSize())){
            try {
                rw.add(allocator.getChangeSize());
                return allocator.allocate();
            } finally {
                memoryLock.unlock();
                doMoreNotifyCheck();
            }
        } else {
            //else的情况直接释放锁
            memoryLock.unlock();
            //触发gc干活
            gcThread.triggerWork();
            synchronized (rw) {
                try {
                    rw.wait();
                } catch (InterruptedException e) {
                }
            }
            return allocateRW(rw, allocator);
        }
    }


    /**
     * 是否notify写
     * @return
     */
    private boolean checkNotifyWrite() {
        return read_wait_count.get() == 0
                || (read_wait_count.get() + LEVEL_LINE) < write_wait_count.get()
                || checkWriteLow();
    }

    /**
     * 写的观察者
     * 写的线程优先唤醒写
     * @return
     */
    private Watcher createWriteWatcher () {
        return new Watcher() {
            public void watch(long change) {
                if(change < 0){
                    if(checkNotifyRead()) {
                        notifyRead();
                    } else {
                       notifyWrite();
                    }
                }
            }
        };
    }



    /**
     * 是否notify读
     * @return
     */
    private boolean checkNotifyRead() {
        return write_wait_count.get() == 0
                ||((write_wait_count.get() + LEVEL_LINE) < read_wait_count.get()
                && !checkWriteLow());
    }

    /**
     * 唤醒一个读
     */
    private void  notifyRead() {
        synchronized (read_size){
            read_size.notify();
        }
    }

    /**
     * 唤醒一个写
     */
    private void  notifyWrite() {
        synchronized (write_size) {
            write_size.notify();
        }
    }

    /**
     * 重设超时时间
     * @param t
     */
    public synchronized void resetTimer(long t) {
        if(timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(createTask(), t, t);
    }



    /**
     * 是否有足够的空间来分配内存
     * @param size
     * @return
     */
    private boolean cs(long size) {
        return  getCurrentMemorySize() + size < MemoryConf.getTotalMemSize();
    }

    private void  gc() {
        while (read_wait_count.get() != 0 && write_wait_count.get() != 0) {
            //TODO releaseList
            //stop 1微妙
            LockSupport.parkNanos(1000);
        }
    }

    /**
     * 判断写空间低于阈值优先唤醒write
     * @return
     */
    private boolean checkWriteLow() {
        //有写的等待线程
        return  write_wait_count.get() > 0
                //写的空间小于总内存的1/8
                && getWriteSize() < ( MemoryConf.getTotalMemSize() << MIN_WRITE_OFFSET)
                //读的空间大于写内存的7倍
                && getReadSize() > (getWriteSize() >> MIN_WRITE_OFFSET  - getWriteSize());
    }

    //定时任务的task
    private TimerTask createTask() {
        return new TimerTask() {
            @Override
            public void run() {



            }
        };
    }

    /**
     * 当前内存大小
     * @return
     */
    public long getCurrentMemorySize(){
        return getReadSize() + getWriteSize();
    }

    /**
     * 获取读内存大小
     * @return
     */
    public long getReadSize(){
        return read_size.get();
    }


    /**
     * 获取写内存大小
     * @return
     */
    public long getWriteSize(){
        return write_size.get();
    }

    public static synchronized void clear() {
        if(instance != null) {
            if(instance.gcThread != null) {
                instance.gcThread.clear();
            }
            instance = null;
        }
    }

}
