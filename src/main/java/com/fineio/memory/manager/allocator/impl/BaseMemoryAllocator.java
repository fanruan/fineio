package com.fineio.memory.manager.allocator.impl;


import com.fineio.memory.MemoryUtils;
import com.fineio.memory.manager.allocator.Allocator;
import com.fineio.memory.manager.allocator.ReAllocator;
import com.fineio.memory.manager.allocator.WriteAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.memory.manager.obj.SyncObject;
import com.fineio.memory.manager.obj.impl.AllocateObject;

import java.io.InputStream;

/**
 * @author yee
 * @date 2018/9/18
 */
public abstract class BaseMemoryAllocator extends SyncObject {

    protected MemoryObject allocateRead(InputStream is, int size) throws OutOfMemoryError {
        throw new UnsupportedOperationException();
    }

    protected MemoryObject allocateRead(InputStream is) throws OutOfMemoryError {
        throw new UnsupportedOperationException();
    }

    /**
     * 内存分配器Builder
     */
    public enum Builder {
        /**
         * 分块文件Builder
         */
        BLOCK {
            @Override
            public Allocator build(final InputStream is, final int maxLength) {
                return new BlockFileMemoryAllocator.BlockFileReadAllocator() {

                    @Override
                    public MemoryObject allocate() throws OutOfMemoryError {
                        return allocateRead(is, maxLength);
                    }
                };
            }

            @Override
            public Allocator build(final InputStream is) {
                return new BlockFileMemoryAllocator.BlockFileReadAllocator() {

                    @Override
                    public MemoryObject allocate() throws OutOfMemoryError {
                        return allocateRead(is);
                    }
                };
            }
        };
        /**
         * 直接单文件Builder
         */

        public abstract Allocator build(InputStream is, int maxLength);

        public abstract Allocator build(InputStream is);

        public ReAllocator build(final long address, final long size, final long newSize) {
            return new BaseReAllocator(address, size, newSize);
        }
    }
}
