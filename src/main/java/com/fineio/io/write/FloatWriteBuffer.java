package com.fineio.io.write;

import com.fineio.io.FloatBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class FloatWriteBuffer extends WriteBuffer implements FloatBuffer {
    public static final WriteModel MODEL;

    static {
        MODEL = new WriteModel<FloatBuffer>() {
            @Override
            protected final FloatWriteBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new FloatWriteBuffer(connector, fileBlock, n);
            }

            @Override
            public final FloatWriteBuffer createBuffer(final Connector connector, final URI uri) {
                return new FloatWriteBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_FLOAT;
            }
        };
    }

    private FloatWriteBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private FloatWriteBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_FLOAT;
    }

    @Override
    public final void put(final float n) {
        this.put(++this.max_position, n);
    }

    @Override
    public final void put(final int n, final float n2) {
        this.ensureCapacity(n);
        MemoryUtils.put(this.address, n, n2);
    }

    @Override
    public final float get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getFloat(this.address, n);
    }
}
