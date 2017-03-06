package com.fineio.cache;

import com.fineio.memory.MemoryUtils;

/**
 * Created by daniel on 2017/3/6.
 */
public class ReAllocator implements Allocator{

    private long address;

    private long newSize;

    private long oldSize;

    public ReAllocator(long address, long oldSize, long newSize) {
        this.address = address;
        this.oldSize = oldSize;
        this.newSize = newSize;
    }


    public long getChangeSize() {
        return newSize - oldSize;
    }

    public long allocate() {
        return MemoryUtils.reallocate(address, newSize);
    }
}
