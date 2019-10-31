package com.fineio.memory.manager.manager;

import com.fineio.FineIoService;
import com.fineio.memory.manager.allocator.Allocator;
import com.fineio.memory.manager.allocator.ReadAllocator;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.thread.FineIOExecutors;
import com.sun.management.OperatingSystemMXBean;
import sun.misc.JavaLangRefAccess;
import sun.misc.SharedSecrets;
import sun.misc.VM;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yee
 * @date 2018/9/18
 */
public enum MemoryManager implements FineIoService {
    /**
     * 单例
     */
    INSTANCE;
    /**
     * 触发释放的内存百分比
     */
    private static final double DEFAULT_RELEASE_RATE = 0.8D;
    /**
     * 释放内存上限
     */
    private long releaseLimit;
    /**
     * 扩容上限
     */
    private long memorySizeUpLimit;
    /**
     * 读内存空间
     */
    private volatile AtomicLong readSize;
    /**
     * 写内存空间
     */
    private volatile AtomicLong writeSize;
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
    private ScheduledExecutorService timeoutCleaner;
    private volatile AtomicInteger triggerCount = new AtomicInteger(0);
    private volatile AtomicInteger triggerGcCount = new AtomicInteger(0);

    private Cleaner cleaner;

    @Override
    public void start() {
        this.readSize = new AtomicLong();
        this.writeSize = new AtomicLong();
        currentMaxSize = Math.min(VM.maxDirectMemory(), getMaxSize());
        releaseLimit = (long) (currentMaxSize * DEFAULT_RELEASE_RATE);
        memorySizeUpLimit = (long) (getMaxSize() * DEFAULT_RELEASE_RATE);
        timeoutCleaner = FineIOExecutors.newScheduledExecutorService(1, "fineio-timeout-cleaner");
        timeoutCleaner.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                cleaner.cleanTimeout();
            }
        }, 10, 10, TimeUnit.MINUTES);
    }

    @Override
    public void stop() {
        timeoutCleaner.shutdownNow();
        triggerGcCount.set(0);
        triggerCount.set(0);
        writeWaitCount.set(0);
        readWaitCount.set(0);
    }

    public final void updateRead(long size) {
        readSize.addAndGet(size);
    }

    public final void registerCleaner(Cleaner cleaner) {
        this.cleaner = cleaner;
    }

    public final void updateWrite(long size) {
        writeSize.addAndGet(size);
    }

    public final void flip(long size, boolean isRead) {
        size = Math.abs(size);
        // 如果当前是读内存，转成写内存
        if (isRead) {
            readSize.addAndGet(0 - size);
            writeSize.addAndGet(size);
        } else {
            writeSize.addAndGet(0 - size);
            readSize.addAndGet(size);
        }
    }

    public final MemoryObject allocate(Allocator allocator) {
        return checkMemory(allocator);
    }

    private final MemoryObject checkMemory(Allocator allocator) {
        memoryLock.lock();
        long checkSize = readSize.get() + writeSize.get() + allocator.getAllocateSize();
        //判断条件加上锁把
        if (checkSize < currentMaxSize) {
            try {
                return allocator.allocate();
            } finally {
                memoryLock.unlock();
            }
        } else {
            try {
                AtomicInteger waitCount = allocator instanceof ReadAllocator ? readWaitCount : writeWaitCount;
                waitCount.incrementAndGet();
                boolean allocateSuccess = new CleanTask(allocator.getAllocateSize()).run();
                waitCount.decrementAndGet();
                if (allocateSuccess) {
                    return allocator.allocate();
                } else {
                    throw new OutOfMemoryError("fineio not enough memory");
                }
            } finally {
                memoryLock.unlock();
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
        boolean cleanTimeout();

        boolean cleanOne();

        void cleanReadable();
    }

    private class CleanTask {

        private long allocateSize;

        public CleanTask(long allocateSize) {
            this.allocateSize = allocateSize;
        }

        public boolean run() {

            if (null != cleaner) {
                final JavaLangRefAccess jlra = SharedSecrets.getJavaLangRefAccess();
                //先把虚引用全清理了
                while (jlra.tryHandlePendingReference()) {
                    if (isMemoryEnough()) {
                        return true;
                    }
                }
                //内存不够就清理超时的，触发gc再清理虚引用
                if (cleaner.cleanTimeout()) {
                    System.gc();
                    while (jlra.tryHandlePendingReference()) {
                        if (isMemoryEnough()) {
                            return true;
                        }
                    }
                }
                //还不够就随机丢掉一个，丢10次，再失败就没内存了
                boolean interrupted = false;
                try {
                    long sleepTime = 1;
                    int loopCount = 0;
                    while (true) {
                        cleaner.cleanOne();
                        //释放不掉就等一会
                        if (!jlra.tryHandlePendingReference()) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                interrupted = true;
                            }
                        }
                        if (isMemoryEnough()) {
                            return true;
                        }
                        sleepTime <<= 1;
                        //等太多次就gc下
                        if (sleepTime > 100) {
                            System.gc();
                        }
                        if (loopCount++ > 10) {
                            break;
                        }
                    }
                    return false;

                } finally {
                    if (interrupted) {
                        Thread.currentThread().interrupt();
                    }
                }

            }
            return false;
        }

        public boolean isMemoryEnough() {
            return (readSize.get() + writeSize.get() + allocateSize) < releaseLimit;
        }
    }
}
