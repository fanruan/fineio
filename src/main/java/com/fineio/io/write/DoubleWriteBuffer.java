package com.fineio.io.write;

import com.fineio.io.DoubleBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class DoubleWriteBuffer extends WriteBuffer implements DoubleBuffer {
    public static final WriteModel MODEL;

    static {
        MODEL = new WriteModel<DoubleBuffer>() {
            @Override
            protected final DoubleWriteBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new DoubleWriteBuffer(connector, fileBlock, n);
            }

            @Override
            public final DoubleWriteBuffer createBuffer(final Connector connector, final URI uri) {
                return new DoubleWriteBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_DOUBLE;
            }
        };
    }

    private DoubleWriteBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private DoubleWriteBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_DOUBLE;
    }

    @Override
    public final void put(final double n) {
        this.put(++this.max_position, n);
    }

    @Override
    public final void put(final int n, final double n2) {
        this.ensureCapacity(n);
        MemoryUtils.put(this.address, n, n2);
    }

    @Override
    public final double get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getDouble(this.address, n);
    }
}
