package com.fineio.io.base;

import com.fineio.exception.StreamCloseException;
import com.fineio.memory.MemoryUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daniel on 2017/2/23.
 */
public final class DirectInputStream extends InputStream {
    private final static int EOF = -1;

    private int p = 0;
    private long address;
    private int size;
    private Checker checker;

    DirectInputStream(long address, int size, Checker checker) {
        this.address = address;
        this.size = size;
        this.checker = checker;
    }

    /**
     * 流长度
     * @return
     */
    public long size(){
        return size;
    }

    public final int read(byte b[], int off, int len) throws IOException {
        if (doLenCheck(b, off, len)){
            return 0;
        }
        return readMemory(b, off, len);
    }

    private  final int readMemory(byte[] b, int off, int len) {
        return readInner(b, off, getLen(len,off));
    }

    private final int readInner(byte[] b, int off, int len) {
        return len == 0 ? EOF : ri(b, off, len);
    }

    private final int ri(byte[] b, int off, int len) {
        MemoryUtils.readMemory(b, off, address + p, len);
        p+=len;
        return len;
    }

    private final boolean doLenCheck(byte[] b, int off, int len) {
        doCheck();
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return true;
        }
        return false;
    }

    private final int getLen(int len,int off) {
        int left = size - p - off;
        if(left < len) {
            len = left;
        }
        if(len < 0){
            len = 0;
        }
        return len;
    }

    @Override
    public final int read() throws IOException {
        doCheck();
        return p == size ? EOF : (MemoryUtils.getByte(address, p++)&0xff);
    }

    private final void doCheck() {
        if(checker != null && !checker.check()) {
            throw new StreamCloseException();
        }
    }
}
