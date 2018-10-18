package com.fineio.io.read;

import com.fineio.io.DoubleBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class DoubleReadBuffer extends ReadBuffer implements DoubleBuffer {
    public static final ReadModel MODEL;

    static {
        MODEL = new ReadModel<DoubleBuffer>() {
            @Override
            protected final DoubleReadBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new DoubleReadBuffer(connector, fileBlock, n);
            }

            @Override
            public final DoubleReadBuffer createBuffer(final Connector connector, final URI uri) {
                return new DoubleReadBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_DOUBLE;
            }
        };
    }

    private DoubleReadBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private DoubleReadBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_DOUBLE;
    }

    @Override
    public final double get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getDouble(this.address, n);
    }
}
