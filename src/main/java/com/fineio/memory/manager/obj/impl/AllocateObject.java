package com.fineio.memory.manager.obj.impl;

import com.fineio.memory.manager.obj.MemoryObject;

/**
 * @author yee
 * @date 2018/9/18
 */
public class AllocateObject implements MemoryObject {

    private long address;
    private long allocateSize;

    public AllocateObject(long address, long allocateSize) {
        this.address = address;
        this.allocateSize = allocateSize;
    }

    public AllocateObject() {
    }

    public AllocateObject(long address) {
        this.address = address;
    }

    @Override
    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    @Override
    public long getAllocateSize() {
        return allocateSize;
    }

    public void setAllocateSize(long allocateSize) {
        this.allocateSize = allocateSize;
    }
}
