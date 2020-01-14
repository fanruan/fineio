package com.fineio.io.base;

import com.fineio.exception.StreamCloseException;
import com.fineio.memory.MemoryUtils;

import java.io.InputStream;

/**
 * Created by daniel on 2017/2/23.
 */
public final class DirectInputStream extends InputStream {
    private final static int EOF = -1;

    private Checker checker;

    private final long address;
    private final int size;
    private int cursor = 0;

    @Override
    public int read() {
        doCheck();
        return available() > 0 ? MemoryUtils.getByte(address, cursor++) : EOF;
    }

    @Override
    public int read(byte[] b, int off, int len) {
        doCheck();
        int avail = available();
        if (avail <= 0) {
            return EOF;
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

    public DirectInputStream(long address, int size, Checker checker) {
        this.address = address;
        this.size = size;
        this.checker = checker;
    }

    /**
     * 流长度
     * @return
     */
    public long size() {
        return size;
    }

    private final void doCheck() {
        if (checker != null && !checker.check()) {
            throw new StreamCloseException();
        }
    }
}
