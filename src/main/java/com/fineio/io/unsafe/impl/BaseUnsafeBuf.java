package com.fineio.io.unsafe.impl;

import com.fineio.exception.BufferConstructException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.Level;
import com.fineio.io.base.BufferKey;
import com.fineio.io.unsafe.ByteUnsafeBuf;
import com.fineio.io.unsafe.UnsafeBuf;
import com.fineio.memory.manager.allocator.Allocator;
import com.fineio.memory.manager.allocator.impl.BaseMemoryAllocator;
import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.memory.manager.obj.impl.AllocateObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class BaseUnsafeBuf implements UnsafeBuf {
    private BufferKey bufferKey;
    private long address;
    private long memorySize;
    private AtomicBoolean close = new AtomicBoolean(false);
    private int maxSize;
    private int offset;
    private int maxOffset;
    private int currentOffset;
    private int currentMaxSize = 10;
    private Level level;
    private ReentrantLock lock = new ReentrantLock();

    public static ByteUnsafeBuf newBuffer(BufferKey bufferKey) {
        return new ByteUnsafeBufImpl(new BaseUnsafeBuf(bufferKey));
    }

    public static ByteUnsafeBuf newBuffer(BufferKey bufferKey, int maxOffset) {
        return new ByteUnsafeBufImpl(new BaseUnsafeBuf(bufferKey, maxOffset));
    }

    private BaseUnsafeBuf(BufferKey bufferKey) {
        this.bufferKey = bufferKey;
        this.level = Level.READ;
        loadContent();
    }

    private BaseUnsafeBuf(BufferKey bufferKey, int maxOffset) {
        this.bufferKey = bufferKey;
        this.maxOffset = maxOffset;
        int maxMemory = 1 << (maxOffset + offset);
        this.maxSize = maxMemory >> offset;
        this.level = Level.WRITE;
    }

    @Override
    public void close() throws IOException {
        if (close.compareAndSet(false, true)) {
            AllocateObject memoryObject = new AllocateObject(address, memorySize);
            if (level == Level.WRITE) {
                BaseDeAllocator.Builder.WRITE.build().deAllocate(memoryObject);
            } else {
                BaseDeAllocator.Builder.READ.build().deAllocate(memoryObject);
            }
        }
    }

    private void loadContent() {
        try {
            MemoryObject allocate = MemoryManager.INSTANCE.allocate(BaseMemoryAllocator.Builder.BLOCK.build(bufferKey.getConnector().read(bufferKey.getBlock())));
            address = allocate.getAddress();
            this.memorySize = allocate.getAllocateSize();
            this.maxSize = (int) (this.memorySize >> offset);
            this.close.compareAndSet(true, false);
        } catch (IOException e) {
            throw new BufferConstructException(e);
        }
    }

    BaseUnsafeBuf setOffset(int offset) {
        this.offset = offset;
        this.maxSize = (int) (memorySize >> offset);
        if (maxOffset != 0) {
            int maxMemory = 1 << (maxOffset + offset);
            this.maxSize = maxMemory >> offset;
        }
        return this;
    }

    int ensurePos(int pos) {
        if (0 != address && pos > -1 && pos < maxSize) {
            return pos;
        }
        lock.lock();
        try {
            if (0 != address) {
                loadContent();
            }
            return ensurePos(pos);
        } finally {
            lock.unlock();
        }
    }

    int ensureCap(int pos) {
        if (pos < maxSize && !close.get()) {
            addCapacity(pos);
            return pos;
        } else {
            throw new BufferIndexOutOfBoundsException(bufferKey.getBlock().getBlockURI(), pos, maxSize);
        }
    }

    private void addCapacity(int position) {
        while (position >= currentMaxSize) {
            addCapacity();
        }
    }

    private void setCurrentCapacity(int offset) {
        this.currentOffset = offset;
        this.currentMaxSize = 1 << offset;
    }

    private void addCapacity() {
        int len = this.currentMaxSize << offset;
        setCurrentCapacity(this.currentOffset + 1);
        int newLen = this.currentMaxSize << offset;
        Allocator allocator = BaseMemoryAllocator.Builder.BLOCK.build(address, len, newLen);
        MemoryObject object = MemoryManager.INSTANCE.allocate(allocator);
        address = object.getAddress();
        memorySize = newLen;
    }

    long getAddress() {
        return address;
    }

}
