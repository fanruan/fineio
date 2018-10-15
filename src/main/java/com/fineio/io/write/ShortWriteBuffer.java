package com.fineio.io.write;

import com.fineio.io.ShortBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class ShortWriteBuffer extends WriteBuffer implements ShortBuffer {
    public static final WriteModel MODEL;

    static {
        MODEL = new WriteModel<ShortBuffer>() {
            @Override
            protected final ShortWriteBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new ShortWriteBuffer(connector, fileBlock, n);
            }

            @Override
            public final ShortWriteBuffer createBuffer(final Connector connector, final URI uri) {
                return new ShortWriteBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_SHORT;
            }
        };
    }

    private ShortWriteBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private ShortWriteBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_SHORT;
    }

    @Override
    public final void put(final short n) {
        this.put(++this.max_position, n);
    }

    @Override
    public final void put(final int n, final short n2) {
        this.ensureCapacity(n);
        MemoryUtils.put(this.address, n, n2);
    }

    @Override
    public final short get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getShort(this.address, n);
    }
}
