package com.fineio.v3.memory.allocator;

import com.fineio.accessor.FileMode;
import com.fineio.v3.exception.OutOfDirectMemoryException;

/**
 * @author yee
 */
public interface MemoryAllocator {

    /**
     * 申请内存
     *
     * @param size
     * @param mode
     */
    long allocate(long size, FileMode mode) throws OutOfDirectMemoryException;

    /**
     * 释放内存
     *
     * @param address
     * @param size
     */
    void release(long address, long size, FileMode mode);

    /**
     * 当前内存大小
     *
     * @return
     */
    long getMemory();

    void clear();

//    void addMemory(long size, FileMode mode) throws OutOfDirectMemoryException;
}