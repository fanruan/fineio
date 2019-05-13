package com.fineio.v3.memory;

import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.MemoryHelper;
import com.fineio.v3.memory.allocator.BaseMemoryAllocator;
import com.fineio.v3.memory.allocator.MemoryAllocator;
import com.fineio.v3.memory.allocator.MemoryReAllocator;
import com.fineio.v3.memory.allocator.WriteMemoryAllocator;
import com.fineio.v3.type.FileMode;

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

    MemoryManager() {
        long total = MemoryHelper.getMaxMemory();
        this.allocator = new BaseMemoryAllocator((long) (total * 0.6));
        this.reAllocator = new WriteMemoryAllocator((long) (total * 0.2));
    }

    public long allocate(long size, FileMode mode) {
        mode.getLock().lock();
        try {
            Condition condition = mode.getLock().newCondition();
            if (mode == FileMode.READ) {
                return allocator.allocate(size, condition);
            }
            return reAllocator.allocate(size, condition);
        } catch (InterruptedException e) {
            FineIOLoggers.getLogger().warn(e);
            return 0;
        } finally {
            mode.getLock().unlock();
        }
    }

    public long allocate(long address, long oldSize, long newSize) {
        FileMode.WRITE.getLock().lock();
        try {
            return reAllocator.reallocate(address, oldSize, newSize, FileMode.WRITE.getLock().newCondition());
        } catch (InterruptedException e) {
            FineIOLoggers.getLogger().warn(e);
            FileMode.WRITE.getLock().unlock();
            release(address, oldSize, FileMode.WRITE);
            return 0;
        } finally {
            FileMode.WRITE.getLock().unlock();
        }
    }

    public void release(long address, long size, FileMode mode) {
        mode.getLock().lock();
        try {
            Condition condition = mode.getLock().newCondition();
            if (mode == FileMode.READ) {
                allocator.release(address, size, condition);
            } else {
                reAllocator.release(address, size, condition);
            }
        } finally {
            mode.getLock().unlock();
        }
    }
}
