package com.fineio.v3.file.sync;

import com.fineio.v3.memory.MemoryUtils;

import java.io.InputStream;

/**
 * @author anchore
 * @date 2019/4/15
 */
class DirectMemoryInputStream extends InputStream {
    private final long address;
    private final int size;
    private int cursor = 0;

    public DirectMemoryInputStream(long address, int size) {
        this.address = address;
        this.size = size;
    }

    @Override
    public int read() {
        return available() > 0 ? MemoryUtils.getByte(address, cursor++) : -1;
    }

    @Override
    public int read(byte[] b, int off, int len) {
        int avail = available();
        if (avail <= 0) {
            return -1;
        }
        int size = avail > len ? len : avail;
        MemoryUtils.readMemory(b, off, address + cursor, size);
        cursor += size;
        return size;
    }

    @Override
    public int available() {
        return size - cursor;
    }
}