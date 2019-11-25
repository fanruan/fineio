package com.fineio.v3.memory.allocator;

import com.fineio.accessor.FileMode;
import com.fineio.v3.exception.OutOfDirectMemoryException;
import com.fineio.v3.memory.MemoryUtils;
import sun.misc.JavaLangRefAccess;
import sun.misc.SharedSecrets;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Condition;

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
    protected LongAdder memorySize;

    protected final int MAX_RETRY_TIME = 10;
    protected final JavaLangRefAccess jlra = SharedSecrets.getJavaLangRefAccess();

    public BaseMemoryAllocator(long limitMemorySize) {
        this.limitMemorySize = limitMemorySize;
        this.memorySize = new LongAdder();
    }

    public void cleanBeforeAllocate(long size) {
        if (memorySize.sum() + size >= limitMemorySize) {
            while (jlra.tryHandlePendingReference()) {
                if (memorySize.sum() + size < limitMemorySize) {
                    break;
                }
            }
            if (memorySize.sum() + size >= limitMemorySize) {
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
                if (memorySize.sum() + size < limitMemorySize) {
                    memorySize.add(size);
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
            this.memorySize.add(0 - size);
            mode.getCondition().signalAll();
        } finally {
            mode.getLock().unlock();
        }

    }

    @Override
    public long getMemory() {
        return memorySize.sum();
    }

    @Override
    public void clear() {
        memorySize.reset();
    }

//    @Override
//    public void addMemory(long size, FileMode mode) throws OutOfDirectMemoryException {
//        mode.getLock().lock();
//        Condition condition = mode.getCondition();
//
//        if (size < 0) {
//            this.memorySize.add(size);
//            condition.signalAll();
//        } else {
//            int retryTime = 0;
//            while (true) {
//                retryTime++;
//
//                cleanBeforeAllocate(size);
//
//                mode.getLock().lock();
//                try {
//                    if (memorySize.sum() + size < limitMemorySize) {
//                        memorySize.add(size);
//                        return;
//                    }
//                    try {
//                        if (!condition.await(10, TimeUnit.MINUTES) || retryTime > MAX_RETRY_TIME) {
//                            throw new OutOfDirectMemoryException("Cannot allocate memory size " + size + " for 10 min. Max memory is " + limitMemorySize);
//                        }
//                    } catch (InterruptedException e) {
//                        throw new OutOfDirectMemoryException(e);
//                    }
//                } finally {
//                    mode.getLock().unlock();
//                }
//
//            }
//        }
//    }
}
