package com.fineio.memory.manager.allocator.impl;

import com.fineio.memory.MemoryUtils;
import com.fineio.memory.manager.allocator.ReadAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.memory.manager.obj.impl.AllocateObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2018/9/18
 */
public class DirectFileMemoryAllocator extends BaseMemoryAllocator {
    private static final int DEFAULT_SIZE_STEP = 1024;
    private long allocateSize;

    DirectFileMemoryAllocator() {
    }

    @Override
    protected MemoryObject allocateRead(InputStream is, int size) throws OutOfMemoryError {
        byte[] bytes = new byte[size];
        int len = 0;
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        MemoryObject object;
        if (null == is) {
            throw new RuntimeException("InputStream cannot be null");
        }
        try {
            while ((len = is.read(bytes, 0, bytes.length)) > 0) {
                ba.write(bytes, 0, len);
            }
            bytes = ba.toByteArray();
            int off = bytes.length;
            beforeStatusChange();
            long address = MemoryUtils.allocate(off);
            object = new AllocateObject(address, off);
            MemoryUtils.copyMemory(bytes, address);
            MemoryManager.INSTANCE.updateRead(off);
            allocateSize = off;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != is) {
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
        return allocateRead(is, DEFAULT_SIZE_STEP);
    }

    static abstract class DirectFileReadAllocator extends DirectFileMemoryAllocator implements ReadAllocator {
    }
}
