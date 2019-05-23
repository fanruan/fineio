package com.fineio.v3.buffer.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.BufferAllocateFailedException;
import com.fineio.v3.buffer.BufferClosedException;
import com.fineio.v3.buffer.BufferOutOfBoundException;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.exception.OutOfDirectMemoryException;
import com.fineio.v3.memory.MemoryManager;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author anchore
 * @date 2019/4/16
 */
abstract class BaseDirectBuffer implements DirectBuffer {
    long address;
    /**
     * buffer的容量，最大能容纳的元素个数
     * 如16个byte，16个int
     */
    private int cap;
    /**
     * buffer的大小，实际容纳的元素个数
     * 如8个byte，8个int
     */
    private int size;
    /**
     * 写时能增长到的最大容量
     */
    private final int maxCap;

    private final FileBlock FileBlock;

    private final Offset offset;

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final FileMode fileMode;

    /**
     * for write, may grow cap
     *
     * @param FileBlock file key
     * @param offset  offset
     * @param maxCap  max cap
     */
    BaseDirectBuffer(FileBlock FileBlock, Offset offset, int maxCap, FileMode fileMode) throws BufferAllocateFailedException {
        this(allocate(16, offset, fileMode), 16, FileBlock, offset, maxCap, fileMode);
        this.size = 0;
    }

    private static long allocate(int cap, Offset offset, FileMode fileMode) {
        try {
            return MemoryManager.INSTANCE.allocate(cap << offset.getOffset(), fileMode);
        } catch (OutOfDirectMemoryException e) {
            throw BufferAllocateFailedException.ofAllocate(cap << offset.getOffset(), e);
        }
    }

    /**
     * for read, won't grow cap, cap = maxCap
     * <p>
     * for append, write after read
     *
     * @param address address
     * @param cap     cap
     * @param FileBlock file key
     * @param offset  offset
     * @param maxCap  maxCap
     */
    BaseDirectBuffer(long address, int cap, FileBlock FileBlock, Offset offset, int maxCap, FileMode fileMode) {
        this.FileBlock = FileBlock;
        this.offset = offset;
        this.address = address;
        this.cap = cap;
        this.maxCap = maxCap;
        this.size = cap;
        this.fileMode = fileMode;
    }

    void ensureOpen() {
        if (closed.get()) {
            throw new BufferClosedException(address);
        }
    }

    void ensureCap(int pos) {
        if (pos < cap) {
            return;
        }
        int newCap = cap << 1;
        for (; newCap <= pos && newCap > 0; ) {
            newCap <<= 1;
        }
        if (newCap > cap && newCap <= maxCap) {
            reallocate(newCap);
            cap = newCap;
        }
    }

    private void reallocate(int newCap) {
        int oldSize = cap << offset.getOffset();
        int newSize = newCap << offset.getOffset();
        try {
            address = MemoryManager.INSTANCE.allocate(address, oldSize, newSize);
        } catch (OutOfDirectMemoryException e) {
            throw BufferAllocateFailedException.ofReallocate(address, oldSize, newSize, e);
        }
    }

    void checkPos(int pos) {
        if (pos < 0 || pos >= cap) {
            throw new BufferOutOfBoundException(pos, cap);
        }
    }

    void updateSize(int pos) {
        if (pos >= size) {
            size = pos + 1;
        }
    }

    @Override
    public long getAddress() {
        return address;
    }

    @Override
    public int getSizeInBytes() {
        return size << offset.getOffset();
    }

    @Override
    public FileBlock getFileBlock() {
        return FileBlock;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            MemoryManager.INSTANCE.release(address, cap, fileMode);
        }
    }
}