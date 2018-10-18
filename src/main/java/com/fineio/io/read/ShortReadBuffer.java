package com.fineio.io.read;

import com.fineio.io.ShortBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class ShortReadBuffer extends ReadBuffer implements ShortBuffer {
    public static final ReadModel MODEL;

    static {
        MODEL = new ReadModel<ShortBuffer>() {
            @Override
            protected final ShortReadBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new ShortReadBuffer(connector, fileBlock, n);
            }

            @Override
            public final ShortReadBuffer createBuffer(final Connector connector, final URI uri) {
                return new ShortReadBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_SHORT;
            }
        };
    }

    private ShortReadBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    public ShortReadBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_SHORT;
    }

    @Override
    public final short get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getShort(this.address, n);
    }
}
