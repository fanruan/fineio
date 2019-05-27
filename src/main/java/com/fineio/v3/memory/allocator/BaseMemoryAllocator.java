package com.fineio.v3.memory.allocator;

import com.fineio.v3.exception.OutOfDirectMemoryException;
import com.fineio.v3.memory.MemoryUtils;

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


    public BaseMemoryAllocator(long limitMemorySize) {
        this.limitMemorySize = limitMemorySize;
        this.memorySize = new LongAdder();
    }

    /**
     * @param size      allocate size
     * @param condition
     */
    @Override
    public long allocate(long size, Condition condition) throws OutOfDirectMemoryException {
        do {
            if (memorySize.sum() + size < limitMemorySize) {
                memorySize.add(size);
                return MemoryUtils.allocate(size);
            }
            try {
                if (!condition.await(10, TimeUnit.MINUTES)) {
                    throw new OutOfDirectMemoryException("Cannot allocate memory size " + size + " for 10 min. Max memory is " + limitMemorySize);
                }
            } catch (InterruptedException e) {
                throw new OutOfDirectMemoryException(e);
            }
        } while (true);
    }

    /**
     * @param address target address
     * @param size    memory size
     */
    @Override
    public void release(long address, long size, Condition condition) {
        MemoryUtils.free(address);
        this.memorySize.add(0 - size);
        condition.signalAll();
    }

    @Override
    public long getMemory() {
        return memorySize.sum();
    }

    @Override
    public void clear() {
        memorySize.reset();
    }

    @Override
    public void addMemory(long size, Condition condition) throws OutOfDirectMemoryException {
        if (size < 0) {
            this.memorySize.add(size);
            condition.signalAll();
        } else {
            do {
                if (memorySize.sum() + size < limitMemorySize) {
                    memorySize.add(size);
                }
                try {
                    if (!condition.await(10, TimeUnit.MINUTES)) {
                        throw new OutOfDirectMemoryException("Cannot allocate memory size " + size + " for 10 min. Max memory is " + limitMemorySize);
                    }
                } catch (InterruptedException e) {
                    throw new OutOfDirectMemoryException(e);
                }
            } while (true);
        }
    }
}