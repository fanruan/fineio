package com.fineio.v3.memory;

/**
 * @author yee
 */
public interface MemoryManager {

    /**
     * @param size
     */
    long allocate(long size);

    /**
     * @param address
     * @param size
     */
    void release(long address, long size);

}