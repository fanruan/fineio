package com.fineio.v3.memory.allocator;

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
    public long allocate(long size, Condition condition) throws InterruptedException {
        do {
            if (memorySize.sum() + size < limitMemorySize) {
                memorySize.add(size);
                return MemoryUtils.allocate(size);
            }
            if (!condition.await(10, TimeUnit.MINUTES)) {
                throw new OutOfMemoryError("Cannot allocate memory size " + size + " for 10 min. Max memory is " + limitMemorySize);
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

}