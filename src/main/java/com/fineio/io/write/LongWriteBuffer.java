package com.fineio.io.write;

import com.fineio.io.LongBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class LongWriteBuffer extends WriteBuffer implements LongBuffer {
    public static final WriteModel MODEL;

    static {
        MODEL = new WriteModel<LongBuffer>() {
            @Override
            protected final LongWriteBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new LongWriteBuffer(connector, fileBlock, n, null);
            }

            @Override
            public final LongWriteBuffer createBuffer(final Connector connector, final URI uri) {
                return new LongWriteBuffer(connector, uri, null);
            }

            @Override
            protected final byte offset() {
                return 3;
            }
        };
    }

    private LongWriteBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private LongWriteBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return 3;
    }

    @Override
    public final void put(final long n) {
        this.put(++this.max_position, n);
    }

    @Override
    public final void put(final int n, final long n2) {
        this.ensureCapacity(n);
        MemoryUtils.put(this.address, n, n2);
    }

    @Override
    public final long get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getLong(this.address, n);
    }
}
