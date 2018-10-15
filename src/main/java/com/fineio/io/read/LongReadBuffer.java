package com.fineio.io.read;

import com.fineio.io.LongBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class LongReadBuffer extends ReadBuffer implements LongBuffer {
    public static final ReadModel MODEL;

    static {
        MODEL = new ReadModel<LongBuffer>() {
            @Override
            protected final LongReadBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new LongReadBuffer(connector, fileBlock, n, null);
            }

            @Override
            public final LongReadBuffer createBuffer(final Connector connector, final URI uri) {
                return new LongReadBuffer(connector, uri, null);
            }

            @Override
            protected final byte offset() {
                return 3;
            }
        };
    }

    private LongReadBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private LongReadBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return 3;
    }

    @Override
    public final long get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getLong(this.address, n);
    }
}
