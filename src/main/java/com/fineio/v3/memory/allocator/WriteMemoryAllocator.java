package com.fineio.v3.memory.allocator;

import com.fineio.accessor.FileMode;
import com.fineio.v3.exception.OutOfDirectMemoryException;
import com.fineio.v3.memory.MemoryUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2019-04-11
 */
public class WriteMemoryAllocator extends BaseMemoryAllocator implements MemoryReAllocator {
    public WriteMemoryAllocator(long limitMemorySize) {
        super(limitMemorySize);
    }

    @Override
    public long allocate(long size, FileMode mode) throws OutOfDirectMemoryException {
        long address = super.allocate(size, mode);
        MemoryUtils.fill0(address, size);
        return address;
    }

    @Override
    public long reallocate(long address, long oldSize, long newSize, FileMode mode) throws OutOfDirectMemoryException {
        mode.getLock().lock();
        try {
            long addSize = newSize - oldSize;
            if (addSize < 0) {
                throw new IllegalArgumentException("new Size must grater than oldSize");
            } else if (addSize == 0) {
                return address;
            } else {
                do {
                    if (memorySize.get() + addSize < limitMemorySize) {
                        memorySize.addAndGet(addSize);
                        long reallocate = MemoryUtils.reallocate(address, newSize);
                        MemoryUtils.fill0(reallocate + oldSize, addSize);
                        return reallocate;
                    }
                    try {
                        if (!mode.getCondition().await(10, TimeUnit.MINUTES)) {
                            throw new OutOfDirectMemoryException("Cannot allocate memory size " + addSize + " for 10 min. Max memory is " + limitMemorySize);
                        }
                    } catch (InterruptedException e) {
                        throw new OutOfDirectMemoryException(e);
                    }
                } while (true);
            }
        } finally {
            mode.getLock().unlock();
        }
    }
}
