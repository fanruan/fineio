package com.fineio.io.write;

import com.fineio.io.CharBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class CharWriteBuffer extends WriteBuffer implements CharBuffer {
    public static final WriteModel MODEL;

    static {
        MODEL = new WriteModel<CharBuffer>() {
            @Override
            protected final CharWriteBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new CharWriteBuffer(connector, fileBlock, n);
            }

            @Override
            public final CharWriteBuffer createBuffer(final Connector connector, final URI uri) {
                return new CharWriteBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_CHAR;
            }
        };
    }

    private CharWriteBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private CharWriteBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_CHAR;
    }

    @Override
    public final void put(final char c) {
        this.put(++this.max_position, c);
    }

    @Override
    public final void put(final int n, final char c) {
        this.ensureCapacity(n);
        MemoryUtils.put(this.address, n, c);
    }

    @Override
    public final char get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getChar(this.address, n);
    }
}
