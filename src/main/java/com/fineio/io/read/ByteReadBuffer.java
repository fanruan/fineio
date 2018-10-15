package com.fineio.io.read;

import com.fineio.io.ByteBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class ByteReadBuffer extends ReadBuffer implements ByteBuffer {
    public static final ReadModel MODEL;

    static {
        MODEL = new ReadModel<ByteBuffer>() {
            @Override
            protected final ByteReadBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new ByteReadBuffer(connector, fileBlock, n);
            }

            @Override
            public final ByteReadBuffer createBuffer(final Connector connector, final URI uri) {
                return new ByteReadBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_BYTE;
            }
        };
    }

    private ByteReadBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private ByteReadBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_BYTE;
    }

    @Override
    public final byte get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getByte(this.address, n);
    }
}
