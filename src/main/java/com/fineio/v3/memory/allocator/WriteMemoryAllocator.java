package com.fineio.v3.memory.allocator;

import com.fineio.v3.exception.OutOfDirectMemoryException;
import com.fineio.v3.memory.MemoryUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @author yee
 * @date 2019-04-11
 */
public class WriteMemoryAllocator extends BaseMemoryAllocator implements MemoryReAllocator {
    public WriteMemoryAllocator(long limitMemorySize) {
        super(limitMemorySize);
    }

    @Override
    public long allocate(long size, Condition condition) throws OutOfDirectMemoryException {
        long address = super.allocate(size, condition);
        MemoryUtils.fill0(address, size);
        return address;
    }

    @Override
    public long reallocate(long address, long oldSize, long newSize, Condition condition) throws OutOfDirectMemoryException {
        long addSize = newSize - oldSize;
        if (addSize < 0) {
            throw new IllegalArgumentException("new Size must grater than oldSize");
        } else if (addSize == 0) {
            return address;
        } else {
            do {
                if (memorySize.sum() + addSize < limitMemorySize) {
                    memorySize.add(addSize);
                    long reallocate = MemoryUtils.reallocate(address, newSize);
                    MemoryUtils.fill0(reallocate + oldSize, addSize);
                    return reallocate;
                }
                try {
                    if (!condition.await(10, TimeUnit.MINUTES)) {
                        throw new OutOfDirectMemoryException("Cannot allocate memory size " + addSize + " for 10 min. Max memory is " + limitMemorySize);
                    }
                } catch (InterruptedException e) {
                    throw new OutOfDirectMemoryException(e);
                }
            } while (true);
        }
    }
}
