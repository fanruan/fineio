package com.fineio.io.edit;

import com.fineio.io.ShortBuffer;
import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class ShortEditBuffer extends EditBuffer implements ShortBuffer {
    public static final EditModel MODEL;

    static {
        MODEL = new EditModel<ShortBuffer>() {
            @Override
            protected final ShortEditBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new ShortEditBuffer(connector, fileBlock, n);
            }

            @Override
            public final ShortEditBuffer createBuffer(final Connector connector, final URI uri) {
                return new ShortEditBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_SHORT;
            }
        };
    }

    private ShortEditBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private ShortEditBuffer(final Connector connector, final URI uri) {
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

    @Override
    public final void put(final short n) {
        this.put(++this.max_position, n);
    }

    @Override
    public final void put(final int n, final short n2) {
        this.ensureCapacity(n);
        this.judeChange(n, n2);
        MemoryUtils.put(this.address, n, n2);
    }

    private final void judeChange(final int n, final short n2) {
        if (!this.changed && n2 != this.get(n)) {
            this.changed = true;
        }
    }
}
