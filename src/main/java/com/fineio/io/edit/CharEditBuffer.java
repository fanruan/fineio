package com.fineio.io.edit;

import com.fineio.io.CharBuffer;
import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class CharEditBuffer extends EditBuffer implements CharBuffer {
    public static final EditModel MODEL;

    static {
        MODEL = new EditModel<CharBuffer>() {
            @Override
            protected final CharEditBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new CharEditBuffer(connector, fileBlock, n);
            }

            @Override
            public final CharEditBuffer createBuffer(final Connector connector, final URI uri) {
                return new CharEditBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_CHAR;
            }
        };
    }

    private CharEditBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private CharEditBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_CHAR;
    }

    @Override
    public final char get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getChar(this.address, n);
    }

    @Override
    public final void put(final char c) {
        this.put(++this.max_position, c);
    }

    @Override
    public final void put(final int n, final char c) {
        this.ensureCapacity(n);
        this.judeChange(n, c);
        MemoryUtils.put(this.address, n, c);
    }

    private void judeChange(final int n, final char c) {
        if (!this.changed && c != this.get(n)) {
            this.changed = true;
        }
    }
}
