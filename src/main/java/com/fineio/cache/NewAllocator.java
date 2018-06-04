package com.fineio.cache;

import com.fineio.memory.MemoryUtils;

/**
 * Created by daniel on 2017/3/6.
 */
public class NewAllocator implements Allocator {

    private long size;

    public NewAllocator(long size){
        this.size = size;
    }

    public long getChangeSize() {
        return size;
    }

    public long allocate() {
        return MemoryUtils.allocate(size);
    }
}
