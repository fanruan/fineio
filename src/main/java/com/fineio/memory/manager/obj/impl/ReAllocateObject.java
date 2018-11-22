package com.fineio.memory.manager.obj.impl;

import com.fineio.memory.manager.obj.ReAllocateMemoryObject;

/**
 * @author yee
 * @date 2018/9/18
 */
public class ReAllocateObject extends AllocateObject implements ReAllocateMemoryObject {
    private long incrementSize;

    public ReAllocateObject(long address, long allocateSize, long incrementSize) {
        super(address, allocateSize);
        this.incrementSize = incrementSize;
    }

    public ReAllocateObject(long address) {
        super(address);
    }

    public ReAllocateObject() {
    }

    @Override
    public long getIncrementSize() {
        return incrementSize;
    }

    public void setIncrementSize(long incrementSize) {
        this.incrementSize = incrementSize;
    }
}
