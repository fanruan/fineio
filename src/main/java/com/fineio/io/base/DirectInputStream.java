package com.fineio.io.base;

import com.fineio.exception.StreamCloseException;
import com.fineio.io.ByteBuffer;

import java.io.InputStream;

/**
 * Created by daniel on 2017/2/23.
 */
public final class DirectInputStream extends InputStream {
    private final static int EOF = -1;
    private final ByteBuffer byteBuffer;
    private final int size;
    private Checker checker;
    private int cursor = 0;

    public DirectInputStream(ByteBuffer byteBuffer, int size, Checker checker) {
        this.byteBuffer = byteBuffer;
        this.size = size;
        this.checker = checker;
    }

    @Override
    public int read() {
        doCheck();
        return available() > 0 ? byteBuffer.getByte(cursor++) & 0xFF : EOF;
    }

    @Override
    public int available() {
        return size - cursor;
    }

    /**
     * 流长度
     *
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
