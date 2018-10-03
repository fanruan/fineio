package com.fineio.memory.manager.manager;

import com.fineio.cache.AtomicWatchLong;
import com.fineio.cache.Watcher;
import com.fineio.memory.manager.allocator.Allocator;
import com.fineio.memory.manager.allocator.ReadAllocator;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.thread.FineIOExecutors;
import com.sun.management.OperatingSystemMXBean;
import sun.misc.VM;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yee
 * @date 2018/9/18
 */
public enum MemoryManager {
    /**
     * 单例
     */
    INSTANCE;
    /**
     * 触发释放的内存百分比
     */
    private static final double DEFAULT_RELEASE_RATE = 0.8D;
    private static final int LEVEL_LINE = 2;
    private static final int MIN_WRITE_OFFSET = 3;
    private static final int TRY_CLEAN_TIME = 100;
    private static final double DEFAULT_INCREMENT_RATE = 1.1D;
    /**
     * 释放内存上限
     */
    private long releaseLimit;
    /**
     * 扩容上限
     */
    private final long memorySizeUpLimit;
    /**
     * 读内存空间
     */
    private volatile AtomicWatchLong readSize;
    /**
     * 写内存空间
     */
    private volatile AtomicWatchLong writeSize;
    /**
     * 当前可扩容最大内存
     */
    private volatile long currentMaxSize;
    /**
     * 读等待数
     */
    private volatile AtomicInteger readWaitCount = new AtomicInteger(0);
    /**
     * 写等待数
     */
    private volatile AtomicInteger writeWaitCount = new AtomicInteger(0);
    private Lock memoryLock = new ReentrantLock();
    private ExecutorService gcThread = FineIOExecutors.newSingleThreadExecutor("io-gc-thread");
    private ScheduledExecutorService gcTrigger = FineIOExecutors.newScheduledExecutorService(1, "io-gc-trigger");
    private ScheduledExecutorService releaseLimitThread = FineIOExecutors.newScheduledExecutorService(1, "io-limit-thread");
    private volatile AtomicInteger releaseLimitCount = new AtomicInteger(0);
    private volatile AtomicInteger triggerCount = new AtomicInteger(0);
    private volatile AtomicInteger triggerGcCount = new AtomicInteger(0);
    private final LinkedBlockingQueue<CleanTask> taskQueue = new LinkedBlockingQueue<CleanTask>();

    private Cleaner cleaner;

    MemoryManager() {
        this.readSize = new AtomicWatchLong(createReadWatcher());
        this.writeSize = new AtomicWatchLong(createWriteWatcher());
        currentMaxSize = Math.min(VM.maxDirectMemory(), getMaxSize());
        releaseLimit = (long) (currentMaxSize * DEFAULT_RELEASE_RATE);
        memorySizeUpLimit = (long) (getMaxSize() * DEFAULT_RELEASE_RATE);
        gcThread.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        CleanTask task = taskQueue.take();
                        if (readWaitCount.get() + writeWaitCount.get() > 0) {
                            if (task.getCheckSize() >= releaseLimit) {
                                releaseLimitCount.incrementAndGet();
                                task.run();
                            }
                        }
                        LockSupport.parkNanos(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        gcTrigger.scheduleAtFixedRate(new CleanOneTask(), 30, 30, TimeUnit.SECONDS);
        releaseLimitThread.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (releaseLimitCount.get() >= 10) {
                    releaseLimit = (long) Math.min(currentMaxSize * DEFAULT_RELEASE_RATE, releaseLimit * DEFAULT_INCREMENT_RATE);
                } else if (releaseLimitCount.get() == 0) {
                    currentMaxSize = Math.min(VM.maxDirectMemory(), getMaxSize());
                    releaseLimit = (long) (currentMaxSize * DEFAULT_RELEASE_RATE);
                }
                releaseLimitCount.set(0);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public final void updateRead(long size) {
        readSize.add(size);
    }

    public final void registerCleaner(Cleaner cleaner) {
        this.cleaner = cleaner;
    }

    public final void updateWrite(long size) {
        writeSize.add(size);
    }

    public final void flip(long size, boolean isRead) {
        size = Math.abs(size);
        // 如果当前是读内存，转成写内存
        if (isRead) {
            readSize.add(0 - size);
            writeSize.add(size);
        } else {
            writeSize.add(0 - size);
            readSize.add(size);
        }
    }

    public final MemoryObject allocate(Allocator allocator) {
        return checkMemory(allocator, checkGC(allocator));
    }

    private final long checkGC(Allocator allocator) {
        long checkSize = readSize.get() + writeSize.get() + allocator.getAllocateSize();
        if (checkSize >= releaseLimit) {
            taskQueue.offer(new CleanTask(allocator.getAllocateSize()));
        }
        return checkSize;
    }

    private final MemoryObject checkMemory(Allocator allocator, long checkSize) {
        memoryLock.lock();
        //判断条件加上锁把
        if (checkSize < currentMaxSize) {
            try {
                return allocator.allocate();
            } finally {
                memoryLock.unlock();
                doMoreNotifyCheck();
            }
        } else {
            memoryLock.unlock();
            taskQueue.offer(new CleanTask(allocator.getAllocateSize()));
            if (allocator instanceof ReadAllocator) {
                readWaitCount.incrementAndGet();
                synchronized (readSize) {
                    try {
                        readSize.wait();
                    } catch (InterruptedException e) {
                    }
                }
                readWaitCount.decrementAndGet();
            } else {
                writeWaitCount.incrementAndGet();
                synchronized (writeSize) {
                    try {
                        writeSize.wait();
                    } catch (InterruptedException e) {
                    }
                }
                writeWaitCount.decrementAndGet();
            }
            return checkMemory(allocator, readSize.get() + writeSize.get() + allocator.getAllocateSize());
        }
    }

    private final Watcher createReadWatcher() {
        return new Watcher() {
            @Override
            public void watch(long change) {
                if (change < 0) {
                    doMoreNotifyCheck();
                }
            }
        };
    }

    private final Watcher createWriteWatcher() {
        return new Watcher() {
            @Override
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

    private final void doMoreNotifyCheck() {
        if (checkNotifyWrite()) {
            notifyWrite();
        } else {
            notifyRead();
        }
    }

    private final boolean checkNotifyWrite() {
        return getReadWaitCount() == 0
                || (getReadWaitCount() + LEVEL_LINE) < getWriteWaitCount()
                || checkWriteLow();
    }

    private boolean checkNotifyRead() {
        return getWriteWaitCount() == 0
                || ((getWriteWaitCount() + LEVEL_LINE) < getReadWaitCount()
                && !checkWriteLow());
    }

    private boolean checkWriteLow() {
        //有写的等待线程
        return getWriteWaitCount() > 0
                //写的空间小于总内存的1/8
                && writeSize.get() < (getFreeMemory() << MIN_WRITE_OFFSET)
                //读的空间大于写内存的7倍
                && readSize.get() > (writeSize.get() >> MIN_WRITE_OFFSET - writeSize.get());
    }

    private final void notifyWrite() {
        if (getWriteWaitCount() > 0) {
            synchronized (writeSize) {
                writeSize.notify();
            }
        }
    }

    private final void notifyRead() {
        if (getReadWaitCount() > 0) {
            synchronized (readSize) {
                readSize.notify();
            }
        }
    }

    public final int getReadWaitCount() {
        return readWaitCount.get();
    }

    public final long getReadSize() {
        return readSize.get();
    }

    public final long getWriteSize() {
        return writeSize.get();
    }

    public final long getCurrentMemorySize() {
        return getReadSize() + getWriteSize();
    }

    public final int getWriteWaitCount() {
        return writeWaitCount.get();
    }

    public final long getReleaseLimit() {
        return releaseLimit;
    }

    private final long getMaxSize() {
        try {
            OperatingSystemMXBean mb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            long max = mb.getTotalPhysicalMemorySize();
            return max - Runtime.getRuntime().maxMemory();
        } catch (Throwable e) {
            //如果发生异常则使用xmx值
            return Runtime.getRuntime().maxMemory();
        }
    }

    private final long getFreeMemory() {
        try {
            OperatingSystemMXBean mb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            return mb.getFreePhysicalMemorySize();
        } catch (Throwable e) {
            //如果发生异常则使用xmx值
            return Runtime.getRuntime().maxMemory();
        }
    }

    public interface Cleaner {
        boolean clean();

        boolean cleanAllCleanable();

        void cleanReadable();
    }

    private class CleanTask implements Runnable {

        private long allocateSize;

        public CleanTask(long allocateSize) {
            this.allocateSize = allocateSize;
        }

        @Override
        public void run() {
            if (null != cleaner) {
                int tryTime = 0;
                boolean triggerGC = true;
                while (!cleaner.clean()) {
                    if (++tryTime >= TRY_CLEAN_TIME / 2) {
                        triggerGC = cleaner.cleanAllCleanable();
                        if (triggerGC) {
                            break;
                        }
                    }
                    if (tryTime >= TRY_CLEAN_TIME) {
                        if (currentMaxSize < memorySizeUpLimit) {
                            currentMaxSize = (long) Math.min(currentMaxSize * DEFAULT_INCREMENT_RATE, memorySizeUpLimit);
                            triggerGC = false;
                            doMoreNotifyCheck();
                        }
                        break;
                    }
                    LockSupport.parkNanos(1000);
                }
                if (triggerGC && triggerGcCount.incrementAndGet() >= 20) {
                    System.gc();
                    triggerGcCount.set(0);
                }
            }
        }

        public long getCheckSize() {
            return readSize.get() + writeSize.get() + allocateSize;
        }
    }

    private class CleanOneTask implements Runnable {

        @Override
        public void run() {
            if (null != cleaner) {
                try {
                    if (cleaner.clean() && triggerGcCount.incrementAndGet() >= 20) {
                        System.gc();
                        triggerGcCount.set(0);
                    } else if (triggerCount.incrementAndGet() > 2) {
                        if (!cleaner.cleanAllCleanable()) {
                            cleaner.cleanReadable();
                        }
                        triggerCount.set(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
