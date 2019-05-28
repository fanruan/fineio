package com.fineio.v3.memory;

import com.fineio.accessor.FileMode;
import com.fineio.memory.MemoryHelper;
import com.fineio.v3.exception.OutOfDirectMemoryException;
import com.fineio.v3.memory.allocator.BaseMemoryAllocator;
import com.fineio.v3.memory.allocator.MemoryAllocator;
import com.fineio.v3.memory.allocator.MemoryReAllocator;
import com.fineio.v3.memory.allocator.WriteMemoryAllocator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;

/**
 * @author yee
 * @date 2019-04-11
 */
public enum MemoryManager {
    //
    INSTANCE;

    private MemoryAllocator allocator;
    private MemoryReAllocator reAllocator;
    private ConcurrentMap<Long, FileMode> memoryMode = new ConcurrentHashMap<>();

    MemoryManager() {
        long total = MemoryHelper.getMaxMemory();
        this.allocator = new BaseMemoryAllocator((long) (total * 0.6));
        this.reAllocator = new WriteMemoryAllocator((long) (total * 0.2));
    }

    public long allocate(long size, FileMode mode) throws OutOfDirectMemoryException {
        mode.getLock().lock();

        try {
            Condition condition = mode.getCondition();
            long address;
            if (mode == FileMode.READ) {
                address = allocator.allocate(size, condition);
            } else {
                address = reAllocator.allocate(size, condition);
            }
            memoryMode.putIfAbsent(address, mode);
            return address;
        } finally {
            mode.getLock().unlock();
        }
    }

    public long allocate(long address, long oldSize, long newSize) throws OutOfDirectMemoryException {
        FileMode.WRITE.getLock().lock();
        try {
            long reallocate = reAllocator.reallocate(address, oldSize, newSize, FileMode.WRITE.getCondition());
            if (address != reallocate) {
                memoryMode.remove(address);
                memoryMode.putIfAbsent(reallocate, FileMode.WRITE);
            }
            return reallocate;
        } finally {
            FileMode.WRITE.getLock().unlock();
        }
    }

    public void release(long address, long size) {
        FileMode mode = memoryMode.get(address);
        mode.getLock().lock();
        try {
            Condition condition = mode.getCondition();
            if (mode == FileMode.READ) {
                allocator.release(address, size, condition);
            } else {
                reAllocator.release(address, size, condition);
            }
            memoryMode.remove(address);
        } finally {
            mode.getLock().unlock();
        }
    }

    public void transfer(long address, long size) throws OutOfDirectMemoryException {
        allocator.addMemory(size, FileMode.READ.getCondition());
        reAllocator.addMemory(0 - size, FileMode.WRITE.getCondition());
        memoryMode.put(address, FileMode.READ);
    }

    public void clear() {
        allocator.clear();
        reAllocator.clear();
    }
}
