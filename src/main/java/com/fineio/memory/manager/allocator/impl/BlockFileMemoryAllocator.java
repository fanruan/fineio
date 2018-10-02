package com.fineio.memory.manager.allocator.impl;

import com.fineio.memory.MemoryUtils;
import com.fineio.memory.manager.allocator.ReadAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.memory.manager.obj.impl.AllocateObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2018/9/18
 */
public class BlockFileMemoryAllocator extends BaseMemoryAllocator {
    private long allocateSize;

    BlockFileMemoryAllocator() {
    }

    @Override
    protected MemoryObject allocateRead(InputStream is, int size) throws OutOfMemoryError {
        byte[] bytes = new byte[size];
        int off = 0;
        int len = 0;
        MemoryObject object;
        if (null == is) {
            throw new RuntimeException("InputStream cannot be null");
        }
        try {
            while ((len = is.read(bytes, off, size - off)) > 0) {
                off += len;
            }
            beforeStatusChange();
            long address = MemoryUtils.allocate(off);
            object = new AllocateObject(address, off);
            MemoryUtils.copyMemory(bytes, address, off);
            MemoryManager.INSTANCE.updateRead(off);
            allocateSize = off;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return object;
    }

    public long getAllocateSize() {
        return allocateSize;
    }

    @Override
    protected MemoryObject allocateRead(InputStream is) throws OutOfMemoryError {
        try {
            return allocateRead(is, is.available());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static abstract class BlockFileReadAllocator extends BlockFileMemoryAllocator implements ReadAllocator {
    }
}
