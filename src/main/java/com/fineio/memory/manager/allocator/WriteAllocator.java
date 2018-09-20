package com.fineio.memory.manager.allocator;

import com.fineio.memory.manager.obj.MemoryObject;

/**
 * @author yee
 * @date 2018/9/19
 */
public interface WriteAllocator extends Allocator {
    /**
     * 申请写内存
     *
     * @return
     * @throws OutOfMemoryError
     */
    @Override
    MemoryObject allocate() throws OutOfMemoryError;
}
