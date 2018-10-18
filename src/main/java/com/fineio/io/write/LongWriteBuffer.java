package com.fineio.io.write;

import com.fineio.io.LongBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class LongWriteBuffer extends WriteBuffer implements LongBuffer {
    public static final WriteModel MODEL;

    static {
        MODEL = new WriteModel<LongBuffer>() {
            @Override
            protected final LongWriteBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new LongWriteBuffer(connector, fileBlock, n);
            }

            @Override
            public final LongWriteBuffer createBuffer(final Connector connector, final URI uri) {
                return new LongWriteBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_LONG;
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
        return MemoryConstants.OFFSET_LONG;
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
