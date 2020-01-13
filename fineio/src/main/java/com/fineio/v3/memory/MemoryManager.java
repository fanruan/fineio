package com.fineio.v3.memory;

import com.fineio.accessor.FileMode;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.MemoryHelper;
import com.fineio.v3.FineIoProperty;
import com.fineio.v3.exception.OutOfDirectMemoryException;
import com.fineio.v3.memory.allocator.BaseMemoryAllocator;
import com.fineio.v3.memory.allocator.MemoryAllocator;
import com.fineio.v3.memory.allocator.MemoryReAllocator;
import com.fineio.v3.memory.allocator.WriteMemoryAllocator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.fineio.accessor.FileMode.WRITE;

/**
 * @author yee
 * @date 2019-04-11
 */
public enum MemoryManager {
    //
    INSTANCE;

    private final long readMemorySize;
    private final long writeMemorySize;
    private MemoryAllocator allocator;
    private MemoryReAllocator reAllocator;
    private ConcurrentMap<Long, FileMode> memoryMode = new ConcurrentHashMap<>();

    MemoryManager() {
        long total = MemoryHelper.getMaxMemory();
        // 60% free or read_mem_limit
        readMemorySize = (long) Math.min((long) (total * 0.6), FineIoProperty.READ_MEM_LIMIT.getValue() * (1 << 30));
        this.allocator = new BaseMemoryAllocator(readMemorySize);
        FineIOLoggers.getLogger().info(String.format("fineio read memory size %d", readMemorySize));

        // 20% free or write_mem_limit
        writeMemorySize = (long) Math.min((long) (total * 0.2), FineIoProperty.WRITE_MEM_LIMIT.getValue() * (1 << 30));
        this.reAllocator = new WriteMemoryAllocator(writeMemorySize);
        FineIOLoggers.getLogger().info(String.format("fineio write memory size %d", writeMemorySize));
    }

    public long allocate(long size, FileMode mode) throws OutOfDirectMemoryException {
        long address;
        if (mode == FileMode.READ) {
            address = allocator.allocate(size, mode);
        } else {
            address = reAllocator.allocate(size, mode);
        }
        memoryMode.putIfAbsent(address, mode);
        return address;
    }

    public long allocate(long address, long oldSize, long newSize) throws OutOfDirectMemoryException {
        long reallocate = reAllocator.reallocate(address, oldSize, newSize, WRITE);
        if (address != reallocate) {
            memoryMode.remove(address);
            memoryMode.putIfAbsent(reallocate, WRITE);
        }
        return reallocate;
    }

    public void release(long address, long size) {
        FileMode mode = memoryMode.get(address);
        if (mode == FileMode.READ) {
            allocator.release(address, size, mode);
        } else {
            reAllocator.release(address, size, mode);
        }
        memoryMode.remove(address);
    }

//    public void transferWriteToRead(long address, long size) throws OutOfDirectMemoryException {
//        if (!memoryMode.containsKey(address) || memoryMode.get(address) != WRITE) {
//            throw new IllegalArgumentException("cannot transfer memory which doesn't exist or isn't write-memory");
//        }
//
//        allocator.addMemory(size, FileMode.READ);
//
//        reAllocator.addMemory(-size, WRITE);
//
//        memoryMode.replace(address, WRITE, READ);
//    }

    public void clear() {
        allocator.clear();
        reAllocator.clear();
    }

    public long getReadMemoryLimit() {
        return readMemorySize;
    }

    public long getReadMemory() {
        return allocator.getMemory();
    }

    public long getWriteMemory() {
        return reAllocator.getMemory();
    }
}
