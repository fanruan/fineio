package com.fineio.io.impl;

import com.fineio.base.Maths;
import com.fineio.exception.BufferConstructException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.Level;
import com.fineio.io.base.BufferKey;
import com.fineio.io.base.Checker;
import com.fineio.io.base.DirectInputStream;
import com.fineio.memory.manager.allocator.Allocator;
import com.fineio.memory.manager.allocator.impl.BaseMemoryAllocator;
import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.memory.manager.obj.impl.AllocateObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class BaseBuffer implements Buffer {
    private BufferKey bufferKey;
    private volatile long address;
    private long memorySize;
    private AtomicBoolean close = new AtomicBoolean(false);
    private int maxSize;
    private int offset;
    private int maxOffset;
    private int currentOffset = 10;
    private int currentMaxSize;
    private int writePos;
    private Level level;
    private URI uri;
    private ReentrantLock lock = new ReentrantLock();

    private BaseBuffer(BufferKey bufferKey) {
        this.bufferKey = bufferKey;
        this.level = Level.READ;
        this.uri = bufferKey.getBlock().getBlockURI();
        loadContent();
    }

    private BaseBuffer(BufferKey bufferKey, int maxOffset) {
        this.bufferKey = bufferKey;
        this.maxOffset = maxOffset;
        int maxMemory = 1 << (maxOffset + offset);
        this.maxSize = maxMemory >> offset;
        this.level = Level.WRITE;
        this.uri = bufferKey.getBlock().getBlockURI();
    }

    public static ByteBuffer newBuffer(BufferKey bufferKey) {
        return new ByteBufferImpl(new BaseBuffer(bufferKey));
    }

    public static ByteBuffer newBuffer(BufferKey bufferKey, int maxOffset) {
        return new ByteBufferImpl(new BaseBuffer(bufferKey, maxOffset));
    }

    @Override
    public void close() throws IOException {
        if (close.compareAndSet(false, true)) {
            AllocateObject memoryObject = new AllocateObject(address, memorySize);
            address = 0L;
            memorySize = 0L;
            if (level == Level.WRITE) {
                BaseDeAllocator.Builder.WRITE.build().deAllocate(memoryObject);
            } else {
                BaseDeAllocator.Builder.READ.build().deAllocate(memoryObject);
            }

        }
    }

    private void loadContent() {
        try {
            synchronized (this) {
                if (address == 0) {
                    MemoryObject allocate = MemoryManager.INSTANCE.allocate(BaseMemoryAllocator.Builder.BLOCK.build(bufferKey.getConnector().read(bufferKey.getBlock())));
                    address = allocate.getAddress();
                    this.memorySize = allocate.getAllocateSize();
                    this.maxSize = (int) (this.memorySize >> offset) + 1;
                    this.close.compareAndSet(true, false);
                }
            }
        } catch (IOException e) {
            throw new BufferConstructException(e);
        }
    }

    BaseBuffer setOffset(int offset) {
        this.offset = offset;
        this.maxSize = (int) (memorySize >> offset) + 1;
        if (maxOffset != 0) {
            int maxMemory = 1 << (maxOffset + offset);
            this.maxSize = (maxMemory >> offset);
        }
        return this;
    }

    int ensurePos(int pos) {
        if (0 == address) {
            lock.lock();
            try {
                loadContent();
                return ensurePos(pos);
            } finally {
                lock.unlock();
            }
        }
        if (pos > -1 && pos < maxSize) {
            return pos;
        }
        throw new BufferIndexOutOfBoundsException(bufferKey.getBlock().getBlockURI(), pos, maxSize);
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
        if (position > writePos) {
            writePos = position;
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

    @Override
    public InputStream asInputStream() {
        return new DirectInputStream(address, (writePos + 1) << offset, new Checker() {
            @Override
            public boolean check() {
                return true;
            }
        });
    }

    @Override
    public BufferKey getBufferKey() {
        return bufferKey;
    }

    @Override
    public long getMemorySize() {
        return memorySize;
    }

    @Override
    public Buffer flip() {
        if (level == Level.READ) {
            level = Level.WRITE;
            final byte blockOffset = bufferKey.getConnector().getBlockOffset();
            maxOffset = blockOffset - this.offset;
            final int offset = Maths.log2(maxSize);
            setCurrentCapacity(offset);
            currentMaxSize = maxSize;
            writePos = maxSize - 1;
            int maxMemory = 1 << blockOffset;
            this.maxSize = (maxMemory >> this.offset);
            MemoryManager.INSTANCE.flip(memorySize, true);
        } else {
            level = Level.READ;
            MemoryManager.INSTANCE.flip(memorySize, false);
        }
        return this;
    }

    @Override
    public URI getUri() {
        return uri;
    }

}
