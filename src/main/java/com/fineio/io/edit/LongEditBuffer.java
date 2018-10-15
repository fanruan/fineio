package com.fineio.io.edit;

import com.fineio.io.LongBuffer;
import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class LongEditBuffer extends EditBuffer implements LongBuffer {
    public static final EditModel MODEL;

    static {
        MODEL = new EditModel<LongBuffer>() {
            @Override
            protected final LongEditBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new LongEditBuffer(connector, fileBlock, n);
            }

            @Override
            public final LongEditBuffer createBuffer(final Connector connector, final URI uri) {
                return new LongEditBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_LONG;
            }
        };
    }

    private LongEditBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private LongEditBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_LONG;
    }

    @Override
    public final long get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getLong(this.address, n);
    }

    @Override
    public final void put(final long n) {
        this.put(++this.max_position, n);
    }

    @Override
    public final void put(final int n, final long n2) {
        this.ensureCapacity(n);
        this.judeChange(n, n2);
        MemoryUtils.put(this.address, n, n2);
    }

    private final void judeChange(final int n, final long n2) {
        if (!this.changed && n2 != this.get(n)) {
            this.changed = true;
        }
    }
}
