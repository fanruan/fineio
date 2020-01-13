package com.fineio.memory.manager.allocator.impl;


import com.fineio.memory.MemoryUtils;
import com.fineio.memory.manager.allocator.ReAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.memory.manager.obj.ReAllocateMemoryObject;
import com.fineio.memory.manager.obj.SyncObject;
import com.fineio.memory.manager.obj.impl.ReAllocateObject;

/**
 * @author yee
 * @date 2018/9/18
 */
public class BaseReAllocator extends SyncObject implements ReAllocator {
    private final long address;
    private final long size;
    private final long newSize;


    public BaseReAllocator(long address, long size, long newSize) {
        this.address = address;
        this.size = size;
        this.newSize = newSize;
    }

    public MemoryObject allocate() throws OutOfMemoryError {
        return reAllocate(address, size, newSize);
    }

    /**
     * 重新分配内存
     *
     * @param address 指定地址
     * @param size    原有大小
     * @param newSize 新的大小
     * @return
     * @throws OutOfMemoryError
     */
    protected ReAllocateMemoryObject reAllocate(long address, long size, long newSize) throws OutOfMemoryError {
        if (size > newSize) {
            throw new OutOfMemoryError();
        }
        if (size == newSize) {
            return new ReAllocateObject(address, size, 0L);
        }
        long allocateSize = newSize - size;
        address = MemoryUtils.reallocate(address, newSize);
        MemoryUtils.fill0(address + size, allocateSize);
        ReAllocateObject object = new ReAllocateObject(address, newSize, allocateSize);
        MemoryManager.INSTANCE.updateWrite(allocateSize);
        return object;
    }

    @Override
    public long getAllocateSize() {
        return newSize - size;
    }
}
