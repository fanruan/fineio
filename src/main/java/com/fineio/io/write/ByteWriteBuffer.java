package com.fineio.io.write;

import com.fineio.io.ByteBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class ByteWriteBuffer extends WriteBuffer implements ByteBuffer {
    public static final WriteModel MODEL;

    static {
        MODEL = new WriteModel<ByteBuffer>() {
            @Override
            protected final ByteWriteBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new ByteWriteBuffer(connector, fileBlock, n, null);
            }

            @Override
            public final ByteWriteBuffer createBuffer(final Connector connector, final URI uri) {
                return new ByteWriteBuffer(connector, uri, null);
            }

            @Override
            protected final byte offset() {
                return 0;
            }
        };
    }

    private ByteWriteBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private ByteWriteBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return 0;
    }

    @Override
    public final void put(final byte b) {
        this.put(++this.max_position, b);
    }

    @Override
    public final void put(final int n, final byte b) {
        this.ensureCapacity(n);
        MemoryUtils.put(this.address, n, b);
    }

    @Override
    public final byte get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getByte(this.address, n);
    }
}
