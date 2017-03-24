package com.fineio.cache;


import com.fineio.base.SingleWaitThread;
import com.fineio.base.Worker;
import com.fineio.exception.FileCloseException;
import com.fineio.io.Buffer;
import com.fineio.memory.MemoryConf;
import com.fr.third.org.apache.poi.hssf.record.formula.functions.Exec;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    private static final int ACTIVE_PERCENT = 10;
    private static final int LEVEL_LINE = 2;
    private static final int MIN_WRITE_OFFSET = 3;
    private volatile long timeout = DEFAULT_TIMER_TIME;
    //内存分配锁
    private Lock memoryLock = new ReentrantLock();
    //GC的线程
    private SingleWaitThread gcThread;
    //超时的timer
    private Timer timer = new Timer();
    //activeTimer
    private Timer activeTimer = new Timer();
    //正在读的内存大小+正在编辑的缓存大小
    private volatile AtomicWatchLong read_size = new AtomicWatchLong();
    //正在读的等待的数量
    private volatile AtomicInteger read_wait_count = new AtomicInteger(0);
    //写的内存大小
    private volatile AtomicWatchLong write_size = new AtomicWatchLong();
    //正在写的等待的数量
    private volatile AtomicInteger write_wait_count = new AtomicInteger(0);

    //读缓存
    private CacheLinkedMap<Buffer> read = new CacheLinkedMap<Buffer>();
    //编辑缓存
    private CacheLinkedMap<Buffer> edit = new CacheLinkedMap<Buffer>();
    //写缓存
    private CacheLinkedMap<Buffer> write = new CacheLinkedMap<Buffer>();

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

    public Buffer registerBuffer(Buffer buffer){
        switch (buffer.getLevel()){
            case READ: {
                read.put(buffer);
                break;
            }
            case EDIT: {
                edit.put(buffer);
                break;
            }
            case WRITE: {
                write.put(buffer);
                break;
            }
        }
        return  buffer;
    }

    private Buffer checkBuffer( Buffer buffer){
        boolean update = false;
        switch (buffer.getLevel()){
            case READ: {
                update = read.contains(buffer);
                break;
            }
            case EDIT: {
                update = edit.contains(buffer);
                break;
            }
            case WRITE: {
                update = write.contains(buffer);
                break;
            }
        }
        if(!update){
            throw new FileCloseException();
        }
        return buffer;
    }

    private Buffer updateBuffer( Buffer buffer){
        boolean update = false;
        switch (buffer.getLevel()){
            case READ: {
                update = read.update(buffer);
                break;
            }
            case EDIT: {
                update = edit.update(buffer);
                break;
            }
            case WRITE: {
                update = write.update(buffer);
                break;
            }
        }
        if(!update){
            throw new FileCloseException();
        }
        return buffer;
    }

    /**
     * 释放buffer的方法
     * @param buffer buffer
     * @param remove 是否彻底删除
     */
    public void releaseBuffer(Buffer buffer, boolean remove) {
        int reduce_size = 0 - buffer.getAllocateSize();
        switch (buffer.getLevel()){
            case READ: {
                read.remove(buffer, remove);
                read_size.add(reduce_size);
                break;
            }
            case EDIT: {
                edit.remove(buffer, remove);
                read_size.add(reduce_size);
                break;
            }
            case WRITE: {
                write.remove(buffer, remove);
                write_size.add(reduce_size);
                break;
            }
        }
    }

    /**
     * 构造函数
     */
    private CacheManager(){
        timer.schedule(createTimeoutTask(), timeout, timeout);
        activeTimer.schedule(createBufferActiveTask(), timeout/ACTIVE_PERCENT, timeout/ACTIVE_PERCENT);
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
    public long allocateRead(Buffer buffer, long size) {
        try {
            read_wait_count.addAndGet(1);
            checkBuffer(buffer);
            long address = allocateRW(read_size, new NewAllocator(size));
            updateBuffer(buffer);
            return address;
        } finally {
            read_wait_count.addAndGet(-1);
        }
    }

    /**
     * 分配写内存 同一个对象只有在被分配之后才有可能被释放 注册的时候并不加入释放队列
     * @param address 旧地址 没有为0
     * @param oldSize 旧大小 没有为0
     * @param newSize 新大小
     * @return
     */
    public long allocateEdit(Buffer buffer, long address, long oldSize, long newSize) {
        try {
            read_wait_count.addAndGet(1);
            checkBuffer(buffer);
            address =  allocateRW(read_size, new ReAllocator(address, oldSize, newSize));
            updateBuffer(buffer);
            return address;
        } finally {
            read_wait_count.addAndGet(-1);
        }
    }

    /**
     * 分配写内存 同一个对象只有在被分配之后才有可能被释放 注册的时候并不加入释放队列
     * @param address 旧地址 没有为0
     * @param oldSize 旧大小 没有为0
     * @param newSize 新大小
     * @return
     */
    public long allocateWrite(Buffer buffer, long address, long oldSize, long newSize) {
        try {
            write_wait_count.addAndGet(1);
            checkBuffer(buffer);
            address =  allocateRW(write_size, new ReAllocator(address, oldSize, newSize));
            updateBuffer(buffer);
            return address;
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
        return getReadWaitCount() == 0
                || (getReadWaitCount() + LEVEL_LINE) < getWriteWaitCount()
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
        return getWriteWaitCount() == 0
                ||((getWriteWaitCount() + LEVEL_LINE) < getReadWaitCount()
                && !checkWriteLow());
    }

    /**
     * 唤醒一个读
     */
    private void  notifyRead() {
        if(getReadWaitCount() > 0) {
            synchronized (read_size) {
                read_size.notify();
            }
        }
    }

    /**
     * 读等待的句柄数
     * @return
     */
    public int getReadWaitCount() {
        return read_wait_count.get();
    }

    /**
     * 唤醒一个写
     */
    private void  notifyWrite() {
        if(getWriteWaitCount() > 0) {
            synchronized (write_size) {
                write_size.notify();
            }
        }
    }

    /**
     * 重设超时时间
     * @param t
     */
    public synchronized void resetTimer(long t) {
        timeout = t;
        if(timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(createTimeoutTask(), timeout, timeout);
        if(activeTimer != null){
            activeTimer.cancel();
        }
        activeTimer = new Timer();
        activeTimer.schedule(createBufferActiveTask(), timeout/ACTIVE_PERCENT, timeout/ACTIVE_PERCENT);
    }



    /**
     * 是否有足够的空间来分配内存
     * @param size
     * @return
     */
    private boolean cs(long size) {
        return  getCurrentMemorySize() + size < MemoryConf.getFreeMemory();
    }

    private void  gc() {
        while (getReadWaitCount() != 0 || getWriteWaitCount() != 0) {
            if(!forceGC()){
                break;
            }
        }
        //stop 1微妙
        LockSupport.parkNanos(1000);
    }

    /**
     * 写等待的句柄数量
     * @return
     */
    public int getWriteWaitCount() {
        return write_wait_count.get();
    }

    public boolean  forceGC(){
        Buffer buffer = read.poll();
        if(buffer != null){
            buffer.clear();
            return true;
        } else {
            buffer = edit.poll();
            if(buffer != null) {
                buffer.clear();
                return true;
            }
        }
        return false;
    }

    /**
     * 判断写空间低于阈值优先唤醒write
     * @return
     */
    private boolean checkWriteLow() {
        //有写的等待线程
        return  getWriteWaitCount() > 0
                //写的空间小于总内存的1/8
                && getWriteSize() < ( MemoryConf.getFreeMemory() << MIN_WRITE_OFFSET)
                //读的空间大于写内存的7倍
                && getReadSize() > (getWriteSize() >> MIN_WRITE_OFFSET  - getWriteSize());
    }

    //定时清超时任务的task
    private TimerTask createTimeoutTask() {
        return new TimerTask() {
            @Override
            public void run() {
                removeTimeout(read);
                removeTimeout(edit);
            }

            private void removeTimeout(CacheLinkedMap<Buffer> map) {
                Iterator<Buffer> iterator = map.iterator();
                while (iterator.hasNext()) {
                    Buffer buffer = iterator.next();
                    if(buffer != null){
                        if(map.getIdle(buffer) > timeout){
                            buffer.clear();
                        }
                    }
                }
            }
        };
    }


    //定时激活buffer的task
    private TimerTask createBufferActiveTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    resetAccess(read);
                    resetAccess(edit);
                    //10秒内有访问全部命中
                    Thread.sleep(10000);
                    activeAccess(read);
                    activeAccess(edit);
                } catch (Throwable e){
                    //doNothing
                }
            }


            private void activeAccess(CacheLinkedMap<Buffer> map) {
                Iterator<Buffer> iterator = map.iterator();
                while (iterator.hasNext()) {
                    Buffer buffer = iterator.next();
                    if(buffer != null && buffer.recentAccess()){
                        try {
                            updateBuffer(buffer);
                        } catch (FileCloseException e) {
                            //file closed
                        }
                    }
                }
            }

            private void resetAccess(CacheLinkedMap<Buffer> map) {
                Iterator<Buffer> iterator = map.iterator();
                while (iterator.hasNext()) {
                    Buffer buffer = iterator.next();
                    if(buffer != null){
                        buffer.resetAccess();
                    }
                }
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

    public static  void clear() {
        CacheManager cm = null;
        synchronized (CacheManager.class) {
            cm = instance;
            instance = null;
        }
        if(cm != null) {
            if(cm.gcThread != null) {
                cm.gcThread.clear();
            }
            if(cm.timer != null) {
                cm.timer.cancel();
            }
            if(cm.activeTimer != null) {
                cm.activeTimer.cancel();
            }
            forceClear(cm.read);
            forceClear(cm.edit);
            forceClear(cm.write);
            cm = null;
        }
    }

    private static void forceClear(CacheLinkedMap<Buffer> map) {
        Iterator<Buffer> iterator = map.iterator();
        while (iterator.hasNext()) {
            Buffer buffer = iterator.next();
            buffer.force();
        }
    }

}
