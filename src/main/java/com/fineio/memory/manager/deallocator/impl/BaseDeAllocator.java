package com.fineio.memory.manager.deallocator.impl;

import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.MemoryUtils;
import com.fineio.memory.manager.deallocator.DeAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.memory.manager.obj.SyncObject;

/**
 * @author yee
 * @date 2018/9/19
 */
public abstract class BaseDeAllocator extends SyncObject implements DeAllocator {

    protected BaseDeAllocator() {
    }

    /**
     * 释放内存
     *
     * @param memoryObject
     */
    @Override
    public void deAllocate(MemoryObject memoryObject) {
        if (null == memoryObject) {
            return;
        }
        FineIOLoggers.getLogger().debug(String.format("auto release address: %d, release size: %d, currentSize: %d", memoryObject.getAddress(), memoryObject.getAllocateSize(), MemoryManager.INSTANCE.getCurrentMemorySize()));
        long address = memoryObject.getAddress();
        MemoryUtils.free(address);
        returnMemory(memoryObject.getAllocateSize());
        FineIOLoggers.getLogger().debug(String.format("after111111 free size: %d", MemoryManager.INSTANCE.getCurrentMemorySize()));
    }

    /**
     * 归还内存空间
     *
     * @param size
     */
    protected abstract void returnMemory(long size);

    public enum Builder {
        /**
         * 写内存释放器
         */
        WRITE {
            @Override
            public DeAllocator build() {
                return new BaseDeAllocator() {
                    @Override
                    protected void returnMemory(long size) {
                        MemoryManager.INSTANCE.updateWrite(size < 0 ? size : 0 - size);
                    }
                };
            }
        },
        /**
         * 读内存释放器
         */
        READ {
            @Override
            public DeAllocator build() {
                return new BaseDeAllocator() {
                    @Override
                    protected void returnMemory(long size) {
                        MemoryManager.INSTANCE.updateRead(size < 0 ? size : 0 - size);
                    }
                };
            }
        };

        public abstract DeAllocator build();
    }
}
