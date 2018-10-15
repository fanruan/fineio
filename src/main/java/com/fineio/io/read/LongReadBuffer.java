package com.fineio.io.read;

import com.fineio.io.LongBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class LongReadBuffer extends ReadBuffer implements LongBuffer {
    public static final ReadModel MODEL;

    static {
        MODEL = new ReadModel<LongBuffer>() {
            @Override
            protected final LongReadBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new LongReadBuffer(connector, fileBlock, n);
            }

            @Override
            public final LongReadBuffer createBuffer(final Connector connector, final URI uri) {
                return new LongReadBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_LONG;
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
        return MemoryConstants.OFFSET_LONG;
    }

    @Override
    public final long get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getLong(this.address, n);
    }
}
