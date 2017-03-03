package com.fineio.cache;



import com.fineio.memory.MemoryConf;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by daniel on 2017/3/1.
 * 内存的总量根据设置值来
 * 写内存和读内存的占比根据访问的情况来控制
 * 如果没有读的进程，那么全力输出写
 * 如果没有写的进程，那么权利输出读
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

    private CacheManager(){
        timer.schedule(createTask(), DEFAULT_TIMER_TIME, DEFAULT_TIMER_TIME);
        read_size.addListener(createReadWatcher());
        write_size.addListener(createWriteWatcher());
    }

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

    private boolean checkNotifyWrite() {
        return (read_size.get() + LEVEL_LINE) < write_size.get() || checkWriteLow();
    }

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

    private boolean checkNotifyRead() {
        return (write_size.get() + LEVEL_LINE) < read_size.get() && !checkWriteLow();
    }

    private void  notifyRead() {
        synchronized (read_size){
            read_size.notify();
        }
    }

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

    private Timer timer = new Timer();

    private volatile AtomicWatchLong read_size = new AtomicWatchLong();
    private volatile AtomicInteger read_wait_count = new AtomicInteger(0);

    private volatile AtomicWatchLong write_size = new AtomicWatchLong();
    private volatile AtomicInteger write_wait_count = new AtomicInteger(0);

    /**
     * 是否有足够的空间来分配内存
     * @param size
     * @return
     */
    private boolean cs(long size) {
        return  getCurrentMemorySize() + size < MemoryConf.getTotalMemSize();
    }

    private void  gc(long size) {

    }

    /**
     * 判断写空间低于阈值优先唤醒write
     * @return
     */
    private boolean checkWriteLow() {
        //有写的等待线程
        return  write_size.get() > 0
                //写的空间小于总内存的1/8
                && getWriteSize() < ( MemoryConf.getTotalMemSize() << MIN_WRITE_OFFSET)
                //读的空间大于写内存的7倍
                && getReadSize() > (getWriteSize() >> MIN_WRITE_OFFSET  - getWriteSize());
    }


    private TimerTask createTask() {
        return new TimerTask() {
            @Override
            public void run() {



            }
        };
    }

    public long getCurrentMemorySize(){
        return getReadSize() + getWriteSize();
    }

    public long getReadSize(){
        return read_size.get();
    }


    public long getWriteSize(){
        return write_size.get();
    }









}
