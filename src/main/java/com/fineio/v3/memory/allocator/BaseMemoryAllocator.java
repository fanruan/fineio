package com.fineio.v3.memory.allocator;

import com.fineio.accessor.FileMode;
import com.fineio.java.JavaVersion;
import com.fineio.v3.exception.OutOfDirectMemoryException;
import com.fineio.v3.memory.MemoryUtils;
import sun.misc.JavaLangRefAccess;
import sun.misc.SharedSecrets;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;

//import java.util.concurrent.atomic.LongAdder;

/**
 * @author yee
 */
public class BaseMemoryAllocator implements MemoryAllocator {

    /**
     *
     */
    protected final long limitMemorySize;
    /**
     *
     */
    protected AtomicLong memorySize;

    protected final int MAX_RETRY_TIME = 10;

    public BaseMemoryAllocator(long limitMemorySize) {
        this.limitMemorySize = limitMemorySize;
        this.memorySize = new AtomicLong();
    }

    public void cleanBeforeAllocate(long size) {
        if (memorySize.get() + size >= limitMemorySize) {
            if (JavaVersion.isOverJava8()) {
                JavaLangRefAccess jlra = SharedSecrets.getJavaLangRefAccess();
                while (jlra.tryHandlePendingReference()) {
                    if (memorySize.get() + size < limitMemorySize) {
                        break;
                    }
                }
            }

            if (memorySize.get() + size >= limitMemorySize) {
                System.gc();
            }
        }
    }

    /**
     * @param size allocate size
     * @param mode
     */
    @Override
    public long allocate(long size, FileMode mode) throws OutOfDirectMemoryException {
        Condition condition = mode.getCondition();
        int retryTime = 0;
        while (true) {
            retryTime++;
            cleanBeforeAllocate(size);
            mode.getLock().lock();
            try {
                if (memorySize.get() + size < limitMemorySize) {
                    memorySize.getAndAdd(size);
                    return MemoryUtils.allocate(size);
                }
                try {
                    if (!condition.await(10, TimeUnit.MINUTES) || retryTime > MAX_RETRY_TIME) {
                        throw new OutOfDirectMemoryException("Cannot allocate memory size " + size + " for 10 min. Max memory is " + limitMemorySize);
                    }
                } catch (InterruptedException e) {
                    throw new OutOfDirectMemoryException(e);
                }
            } finally {
                mode.getLock().unlock();
            }

        }
    }

    /**
     * @param address target address
     * @param size    memory size
     */
    @Override
    public void release(long address, long size, FileMode mode) {
        mode.getLock().lock();
        try {
            MemoryUtils.free(address);
            this.memorySize.getAndAdd(0 - size);
            mode.getCondition().signalAll();
        } finally {
            mode.getLock().unlock();
        }

    }

    @Override
    public long getMemory() {
        return memorySize.get();
    }

    @Override
    public void clear() {
        memorySize.set(0);
    }

}
