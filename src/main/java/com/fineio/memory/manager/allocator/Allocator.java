package com.fineio.memory.manager.allocator;

import com.fineio.memory.manager.obj.MemoryObject;

/**
 * @author yee
 * @date 2018/9/18
 */
public interface Allocator {
    /**
     * 申请内存
     *
     * @return
     * @throws OutOfMemoryError
     */
    MemoryObject allocate() throws OutOfMemoryError;

    long getAllocateSize();
}
