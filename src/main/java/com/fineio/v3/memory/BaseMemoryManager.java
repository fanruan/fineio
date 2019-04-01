package com.fineio.v3.memory;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class BaseMemoryManager implements MemoryManager {

    /**
     *
     */
    private final long limitMemorySize;
    /**
     *
     */
    protected AtomicLong memorySize;


    public BaseMemoryManager(long limitMemorySize) {
        this.limitMemorySize = limitMemorySize;
        this.memorySize = new AtomicLong(0);
    }

    /**
     * @param size allocate size
     */
    @Override
    public long allocate(long size) {
        if (memorySize.get() + size < limitMemorySize) {
            memorySize.addAndGet(size);
            return MemoryUtils.allocate(size);
        }
        return 0L;
    }

    /**
     * @param address target address
     * @param size    memory size
     */
    @Override
    public void release(long address, long size) {
        MemoryUtils.free(address);
        this.memorySize.addAndGet(0 - size);
    }

}