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
        //释放前让出执行时间，让其他线程尤其是读线程先跑一会
        Thread.yield();
        //如果同时执行的线程较少，就wait不包含fullgc的100毫秒，如果超过120毫秒，认为是发生了fullgc，需要重来一次
        long t = System.currentTimeMillis();
        do {
            synchronized (this) {
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    FineIOLoggers.getLogger().debug("release time wait interrupted");
                    beforeStatusChange();
                }
            }
        } while (System.currentTimeMillis() - t > 120);
        //安全起见再让一下吧
        Thread.yield();
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
