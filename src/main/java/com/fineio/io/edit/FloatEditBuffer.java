package com.fineio.io.edit;

import com.fineio.io.FloatBuffer;
import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class FloatEditBuffer extends EditBuffer implements FloatBuffer {
    public static final EditModel MODEL;

    static {
        MODEL = new EditModel<FloatBuffer>() {
            @Override
            protected final FloatEditBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new FloatEditBuffer(connector, fileBlock, n);
            }

            @Override
            public final FloatEditBuffer createBuffer(final Connector connector, final URI uri) {
                return new FloatEditBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_FLOAT;
            }
        };
    }

    private FloatEditBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private FloatEditBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_FLOAT;
    }

    @Override
    public final float get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getFloat(this.address, n);
    }

    @Override
    public final void put(final float n) {
        this.put(++this.max_position, n);
    }

    @Override
    public final void put(final int n, final float n2) {
        this.ensureCapacity(n);
        this.judeChange(n, n2);
        MemoryUtils.put(this.address, n, n2);
    }

    private final void judeChange(final int n, final float n2) {
        if (!this.changed && Float.compare(n2, this.get(n)) != 0) {
            this.changed = true;
        }
    }
}
