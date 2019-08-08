package com.fineio.memory.manager.allocator;

import com.fineio.memory.manager.obj.MemoryObject;

/**
 * @author yee
 * @date 2018/9/19
 */
public interface ReadAllocator extends Allocator {
    /**
     * 申请读内存
     *
     * @return
     * @throws OutOfMemoryError
     */
    @Override
    MemoryObject allocate() throws OutOfMemoryError;
}
