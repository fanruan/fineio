package com.fineio.cache;

import com.fineio.base.QueueWorkerThread;
import com.fineio.base.Worker;
import com.fineio.io.Buffer;
import com.fineio.io.write.WriteOnlyBuffer;
import com.fineio.memory.MemoryConf;
import com.fineio.memory.MemoryHelper;
import sun.misc.VM;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yee
 * @date 2018/5/31
 */
public class MemoryHandler {
    private static final int LEVEL_LINE = 2;
    private static final int MIN_WRITE_OFFSET = 3;
    private static final long TRIGGER_TIME = 30000;
    private volatile AtomicWatchLong read_size = new AtomicWatchLong();
    private volatile static long maxMemory;
    /**
     * 正在读的等待的数量
     */
    private volatile AtomicInteger read_wait_count = new AtomicInteger(0);
    /**
     * 写的内存大小
     */
    private volatile AtomicWatchLong write_size = new AtomicWatchLong();
    /**
     * 正在写的等待的数量
     */
    private volatile AtomicInteger write_wait_count = new AtomicInteger(0);
    private MemoryAllocator allocator = new MemoryAllocator();
    private ScheduledExecutorService gcThreadTrigger;

    private Lock memoryLock = new ReentrantLock();

    private QueueWorkerThread gcThread;

    private GcCallBack gcCallBack;

    private MemoryHandler(GcCallBack gcCallBack) {
        maxMemory = MemoryHelper.getMaxMemory();
        if (VM.isBooted()) {
            maxMemory = Math.min(VM.maxDirectMemory(), maxMemory);
        }
        this.gcCallBack = gcCallBack;
        read_size.addListener(createReadWatcher());
        write_size.addListener(createWriteWatcher());
        gcThread = new QueueWorkerThread(new Worker() {
            @Override
            public void work() {
                gc();
            }
        });
        gcThreadTrigger = new ScheduledThreadPoolExecutor(1);
        gcThreadTrigger.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (getReadWaitCount() != 0 || getWriteWaitCount() != 0) {
                    gcThread.triggerWork();
                }
            }
        }, 0, TRIGGER_TIME, TimeUnit.MILLISECONDS);
    }

    public static MemoryHandler newInstance(GcCallBack gcCallBack) {
        return new MemoryHandler(gcCallBack);
    }

    private void gc() {
        while (getReadWaitCount() != 0 || getWriteWaitCount() != 0) {
            if (!forceGC()) {
                break;
            }
        }
        //stop 1微妙
        LockSupport.parkNanos(1000);
    }

    public boolean forceGC() {
        return gcCallBack.gc();
    }

    public long allocateRead(long size) {
        return allocator.allocateRead(size);
    }

    public long allocateEdit(long address, long oldSize, long newSize) {
        return allocator.allocateEdit(address, oldSize, newSize);
    }

    public long allocateWrite(long address, long oldSize, long newSize) {
        return allocator.allocateWrite(address, oldSize, newSize);
    }

    public void returnMemory(Buffer buffer, BufferPrivilege bufferPrivilege) {
        long size = 0 - buffer.getAllocateSize();
        switch (bufferPrivilege) {
            case WRITABLE:
                write_size.add(size);
                boolean cache = !buffer.isDirect();
                if (buffer instanceof WriteOnlyBuffer) {
                    cache &= !((WriteOnlyBuffer) buffer).needClear();
                }
                if (cache) {
                    read_size.add(0 - size);
                }
                break;
            case EDITABLE:
            case READABLE:
            case CLEANABLE:
                read_size.add(size);
        }
    }

    /**
     * 更多的唤醒工作
     * 优先唤醒读
     * 在分配内存之后的触发上执行
     */
    private void doMoreNotifyCheck() {
        if (checkNotifyWrite()) {
            notifyWrite();
        } else {
            notifyRead();
        }
    }

    /**
     * 读的观察者
     * 读的线程优先唤醒读
     *
     * @return
     */
    private Watcher createReadWatcher() {
        return new Watcher() {
            public void watch(long change) {
                if (change < 0) {
                    if (checkNotifyWrite()) {
                        notifyWrite();
                    } else {
                        notifyRead();
                    }
                }
            }
        };
    }

    /**
     * 是否notify写
     *
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
     *
     * @return
     */
    private Watcher createWriteWatcher() {
        return new Watcher() {
            public void watch(long change) {
                if (change < 0) {
                    if (checkNotifyRead()) {
                        notifyRead();
                    } else {
                        notifyWrite();
                    }
                }
            }
        };
    }

    /**
     * 判断写空间低于阈值优先唤醒write
     *
     * @return
     */
    private boolean checkWriteLow() {
        //有写的等待线程
        return getWriteWaitCount() > 0
                //写的空间小于总内存的1/8
                && getWriteSize() < (MemoryConf.getFreeMemory() << MIN_WRITE_OFFSET)
                //读的空间大于写内存的7倍
                && getReadSize() > (getWriteSize() >> MIN_WRITE_OFFSET - getWriteSize());
    }

    /**
     * 获取写内存大小
     *
     * @return
     */
    public long getWriteSize() {
        return write_size.get();
    }

    /**
     * 获取读内存大小
     *
     * @return
     */
    public long getReadSize() {
        return read_size.get();
    }

    /**
     * 是否notify读
     *
     * @return
     */
    private boolean checkNotifyRead() {
        return getWriteWaitCount() == 0
                || ((getWriteWaitCount() + LEVEL_LINE) < getReadWaitCount()
                && !checkWriteLow());
    }

    /**
     * 唤醒一个读
     */
    private void notifyRead() {
        if (getReadWaitCount() > 0) {
            synchronized (read_size) {
                read_size.notify();
            }
        }
    }

    /**
     * 读等待的句柄数
     *
     * @return
     */
    public int getReadWaitCount() {
        return read_wait_count.get();
    }

    /**
     * 唤醒一个写
     */
    private void notifyWrite() {
        if (getWriteWaitCount() > 0) {
            synchronized (write_size) {
                write_size.notify();
            }
        }
    }

    /**
     * 写等待的句柄数量
     *
     * @return
     */
    public int getWriteWaitCount() {
        return write_wait_count.get();
    }

    /**
     * 是否有足够的空间来分配内存
     *
     * @param size
     * @return
     */
    private boolean cs(long size) {
        return getReadSize() + getWriteSize() + size < maxMemory;
    }

    interface GcCallBack {
        /**
         * 执行GC
         *
         * @return
         */
        boolean gc();
    }

    public static long getMaxMemory() {
        return maxMemory;
    }

    private class MemoryAllocator {
        /**
         * 分配读内存
         *
         * @param size
         * @return
         */
        public long allocateRead(long size) {
            try {
                read_wait_count.addAndGet(1);
                long address = allocateRW(read_size, new NewAllocator(size));
                return address;
            } finally {
                read_wait_count.addAndGet(-1);
            }
        }

        /**
         * 分配写内存 同一个对象只有在被分配之后才有可能被释放 注册的时候并不加入释放队列
         *
         * @param address 旧地址 没有为0
         * @param oldSize 旧大小 没有为0
         * @param newSize 新大小
         * @return
         */
        public long allocateEdit(long address, long oldSize, long newSize) {
            try {
                read_wait_count.addAndGet(1);
                address = allocateRW(read_size, new ReAllocator(address, oldSize, newSize));
                return address;
            } finally {
                read_wait_count.addAndGet(-1);
            }
        }

        /**
         * 分配写内存 同一个对象只有在被分配之后才有可能被释放 注册的时候并不加入释放队列
         *
         * @param address 旧地址 没有为0
         * @param oldSize 旧大小 没有为0
         * @param newSize 新大小
         * @return
         */
        public long allocateWrite(long address, long oldSize, long newSize) {
            try {
                write_wait_count.addAndGet(1);
                address = allocateRW(write_size, new ReAllocator(address, oldSize, newSize));
                return address;
            } finally {
                write_wait_count.addAndGet(-1);
            }
        }

        /**
         * 分配内存的方法
         *
         * @param rw        保存尺寸的对象
         * @param allocator 分配方法
         * @return
         */
        private long allocateRW(AtomicWatchLong rw, Allocator allocator) {
            memoryLock.lock();
            //判断条件加上锁把
            if (cs(allocator.getChangeSize())) {
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
    }

    public void clear() {
        if (gcThread != null) {
            gcThread.clear();
        }
        if (null != gcThreadTrigger) {
            gcThreadTrigger.shutdown();
        }
    }
}
