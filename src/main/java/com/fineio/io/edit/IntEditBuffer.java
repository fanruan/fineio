package com.fineio.io.edit;

import com.fineio.io.IntBuffer;
import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class IntEditBuffer extends EditBuffer implements IntBuffer {
    public static final EditModel MODEL;

    static {
        MODEL = new EditModel<IntBuffer>() {
            @Override
            protected final IntEditBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new IntEditBuffer(connector, fileBlock, n);
            }

            @Override
            public final IntEditBuffer createBuffer(final Connector connector, final URI uri) {
                return new IntEditBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_INT;
            }
        };
    }

    private IntEditBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private IntEditBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_INT;
    }

    @Override
    public final int get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getInt(this.address, n);
    }

    @Override
    public final void put(final int n) {
        this.put(++this.max_position, n);
    }

    @Override
    public final void put(final int n, final int n2) {
        this.ensureCapacity(n);
        this.judeChange(n, n2);
        MemoryUtils.put(this.address, n, n2);
    }

    private final void judeChange(final int n, final int n2) {
        if (!this.changed && n2 != this.get(n)) {
            this.changed = true;
        }
    }
}
