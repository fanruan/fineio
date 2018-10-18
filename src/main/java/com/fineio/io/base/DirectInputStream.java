package com.fineio.io.base;

import com.fineio.exception.StreamCloseException;
import com.fineio.memory.MemoryUtils;

import java.io.InputStream;

public final class DirectInputStream extends InputStream {
    private static final int EOF = -1;
    private int p;
    private long address;
    private int size;
    private Checker checker;

    DirectInputStream(final long address, final int size, final Checker checker) {
        this.p = 0;
        this.address = address;
        this.size = size;
        this.checker = checker;
    }

    public long size() {
        return this.size;
    }

    @Override
    public final int read(final byte[] array, final int n, final int n2) {
        if (this.doLenCheck(array, n, n2)) {
            return 0;
        }
        return this.readMemory(array, n, n2);
    }

    private final int readMemory(final byte[] array, final int n, final int n2) {
        return this.readInner(array, n, this.getLen(n2, n));
    }

    private final int readInner(final byte[] array, final int n, final int n2) {
        return (n2 == 0) ? -1 : this.ri(array, n, n2);
    }

    private final int ri(final byte[] array, final int n, final int n2) {
        MemoryUtils.readMemory(array, n, this.address + this.p, n2);
        this.p += n2;
        return n2;
    }

    private final boolean doLenCheck(final byte[] array, final int n, final int n2) {
        this.doCheck();
        if (array == null) {
            throw new NullPointerException();
        }
        if (n < 0 || n2 < 0 || n2 > array.length - n) {
            throw new IndexOutOfBoundsException();
        }
        return n2 == 0;
    }

    private final int getLen(int n, final int n2) {
        final int n3 = this.size - this.p - n2;
        if (n3 < n) {
            n = n3;
        }
        if (n < 0) {
            n = 0;
        }
        return n;
    }

    @Override
    public final int read() {
        this.doCheck();
        return (this.p == this.size) ? -1 : (MemoryUtils.getByte(this.address, this.p++) & 0xFF);
    }

    private final void doCheck() {
        if (this.checker != null && !this.checker.check()) {
            throw new StreamCloseException();
        }
    }
}
