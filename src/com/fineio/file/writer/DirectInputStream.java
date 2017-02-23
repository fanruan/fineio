package com.fineio.file.writer;

import com.fineio.memory.MemoryUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daniel on 2017/2/23.
 */
public class DirectInputStream extends InputStream {

    private int p = 0;
    private long address;
    private int size;

    public DirectInputStream(long address, int size) {
        this.address = address;
        this.size = size;
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        len = getLen(len);
        MemoryUtils.readMemory(b, off, address + p, len);
        p+=len;
        return len;
    }

    private int getLen(int len) {
        int left = size - p;
        if(left < len) {
            len = left;
        }
        return len;
    }

    @Override
    public int read() throws IOException {
        return MemoryUtils.getByte(address, p++);
    }
}
