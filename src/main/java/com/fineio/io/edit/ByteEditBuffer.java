package com.fineio.io.edit;

import com.fineio.io.ByteBuffer;
import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class ByteEditBuffer extends EditBuffer implements ByteBuffer {
    public static final EditModel MODEL;

    static {
        MODEL = new EditModel<ByteBuffer>() {
            @Override
            protected final ByteEditBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new ByteEditBuffer(connector, fileBlock, n);
            }

            @Override
            public final ByteEditBuffer createBuffer(final Connector connector, final URI uri) {
                return new ByteEditBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_BYTE;
            }
        };
    }

    private ByteEditBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private ByteEditBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_BYTE;
    }

    @Override
    public final void put(final byte b) {
        this.put(++this.max_position, b);
    }

    @Override
    public final void put(final int n, final byte b) {
        this.ensureCapacity(n);
        this.judeChange(n, b);
        MemoryUtils.put(this.address, n, b);
    }

    private void judeChange(final int n, final byte b) {
        if (!this.changed && b != this.get(n)) {
            this.changed = true;
        }
    }

    @Override
    public final byte get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getByte(this.address, n);
    }
}
