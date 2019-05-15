package com.fineio.v3.memory.allocator;

import com.fineio.v3.exception.OutOfDirectMemoryException;

import java.util.concurrent.locks.Condition;

/**
 * @author yee
 * @date 2019-04-11
 */
public interface MemoryReAllocator extends MemoryAllocator {
    /**
     * 重新分配内存 并且将超出oldSize的部分fill0
     *
     * @param address
     * @param oldSize
     * @param newSize
     * @return
     */
    long reallocate(long address, long oldSize, long newSize, Condition condition) throws OutOfDirectMemoryException;
}
