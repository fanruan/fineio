package com.fineio.io.write;

import com.fineio.io.IntBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class IntWriteBuffer extends WriteBuffer implements IntBuffer {
    public static final WriteModel MODEL;

    static {
        MODEL = new WriteModel<IntBuffer>() {
            @Override
            protected final IntWriteBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new IntWriteBuffer(connector, fileBlock, n);
            }

            @Override
            public final IntWriteBuffer createBuffer(final Connector connector, final URI uri) {
                return new IntWriteBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_INT;
            }
        };
    }

    private IntWriteBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private IntWriteBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_INT;
    }

    @Override
    public final void put(final int n) {
        this.put(++this.max_position, n);
    }

    @Override
    public final void put(final int n, final int n2) {
        this.ensureCapacity(n);
        MemoryUtils.put(this.address, n, n2);
    }

    @Override
    public final int get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getInt(this.address, n);
    }
}
