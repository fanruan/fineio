package com.fineio.v3.memory.allocator;

import java.util.concurrent.locks.Condition;

/**
 * @author yee
 */
public interface MemoryAllocator {

    /**
     * @param size
     * @param condition
     */
    long allocate(long size, Condition condition) throws InterruptedException;

    /**
     * @param address
     * @param size
     */
    void release(long address, long size, Condition condition);

    long getMemory();

}