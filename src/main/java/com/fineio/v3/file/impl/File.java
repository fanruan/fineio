package com.fineio.v3.file.impl;

import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author anchore
 * @date 2019/4/16
 */
public abstract class File<B extends DirectBuffer> implements Closeable {
    protected final FileKey fileKey;

    protected final Connector connector;

    protected final ConcurrentMap<Integer, B> buffers = new ConcurrentHashMap<>();

    protected final AtomicBoolean closed = new AtomicBoolean(false);

    protected final Offset offset;

    protected File(FileKey fileKey, Offset offset, Connector connector) {
        this.fileKey = fileKey;
        this.offset = offset;
        this.connector = connector;
    }

    protected abstract B getBuffer(int nthBuf);

    protected int nthBuf(long pos) {
        return (int) (pos >> (connector.getBlockOffset() - offset.getOffset()));
    }

    protected int nthVal(long pos) {
        return (int) (pos & ((1L << connector.getBlockOffset() - offset.getOffset()) - 1));
    }

    protected void checkPos(long pos) {
        if (pos < 0) {
            throw new IllegalArgumentException(String.format("negative pos %d", pos));
        }
    }

    @Override
    public abstract void close();
}