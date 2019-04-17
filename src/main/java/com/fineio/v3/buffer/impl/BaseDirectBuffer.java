package com.fineio.v3.buffer.impl;

import com.fineio.v3.buffer.BufferClosedException;
import com.fineio.v3.buffer.BufferOutOfBoundException;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;

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
    private int maxCap;

    private final FileKey fileKey;

    private final Offset offset;

    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * for write, may grow cap
     *
     * @param fileKey file key
     * @param offset  offset
     * @param maxCap  max cap
     */
    BaseDirectBuffer(FileKey fileKey, Offset offset, int maxCap) {
        this(MemoryUtils.allocate(16 << offset.getOffset()), 16, fileKey, offset);
        this.size = 0;
        this.maxCap = maxCap;
    }

    /**
     * for read, won't grow cap
     *
     * @param address address
     * @param cap     cap
     * @param fileKey file key
     * @param offset  offset
     */
    BaseDirectBuffer(long address, int cap, FileKey fileKey, Offset offset) {
        this.fileKey = fileKey;
        this.offset = offset;
        this.address = address;
        this.cap = cap;
        this.size = cap;
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
        for (; newCap < pos && newCap > 0; ) {
            newCap <<= 1;
        }
        if (newCap > cap && newCap <= maxCap) {
            // TODO: 2019/4/12 anchore 反正这里要扩容buffer
            address = MemoryUtils.reallocate(address, newCap << offset.getOffset());
            cap = newCap;
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
    public FileKey getFileKey() {
        return fileKey;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            // TODO: 2019/4/12 anchore 反正这里要释放内存
            MemoryUtils.free(address);
        }
    }
}