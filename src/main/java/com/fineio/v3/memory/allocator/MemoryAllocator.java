package com.fineio.v3.memory.allocator;

import com.fineio.v3.exception.OutOfDirectMemoryException;

import java.util.concurrent.locks.Condition;

/**
 * @author yee
 */
public interface MemoryAllocator {

    /**
     * 申请内存
     * @param size
     * @param condition
     */
    long allocate(long size, Condition condition) throws OutOfDirectMemoryException;

    /**
     * 释放内存
     * @param address
     * @param size
     */
    void release(long address, long size, Condition condition);

    /**
     * 当前内存大小
     *
     * @return
     */
    long getMemory();

    void clear();

    void addMemory(long size, Condition condition) throws OutOfDirectMemoryException;
}