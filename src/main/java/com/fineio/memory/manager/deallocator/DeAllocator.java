package com.fineio.memory.manager.deallocator;

import com.fineio.memory.manager.obj.MemoryObject;

/**
 * @author yee
 * @date 2018/9/18
 */
public interface DeAllocator {
    /**
     * 释放内存
     *
     * @param memoryObject
     */
    void deAllocate(MemoryObject memoryObject);
}
