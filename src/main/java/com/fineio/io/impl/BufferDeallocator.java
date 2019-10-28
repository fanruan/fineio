package com.fineio.io.impl;

import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.obj.impl.AllocateObject;

/**
 * @author pony
 * @version 1.1
 * Created by pony on 2019/10/28
 */
class BufferDeallocator implements Runnable {
    private long address;
    private long memorySize;

    public BufferDeallocator(long address, long memorySize) {

        this.address = address;
        this.memorySize = memorySize;
    }

    @Override
    public void run() {
        if (address == 0) {
            return;
        }
        AllocateObject memoryObject = new AllocateObject(address, memorySize);
        address = 0;
        BaseDeAllocator.Builder.READ.build().deAllocate(memoryObject);
    }
}
