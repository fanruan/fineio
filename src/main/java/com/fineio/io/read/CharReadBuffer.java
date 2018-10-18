package com.fineio.io.read;

import com.fineio.io.CharBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class CharReadBuffer extends ReadBuffer implements CharBuffer {
    public static final ReadModel MODEL;

    static {
        MODEL = new ReadModel<CharBuffer>() {
            @Override
            protected final CharReadBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new CharReadBuffer(connector, fileBlock, n);
            }

            @Override
            public final CharReadBuffer createBuffer(final Connector connector, final URI uri) {
                return new CharReadBuffer(connector, uri);
            }

            @Override
            protected final byte offset() {
                return MemoryConstants.OFFSET_CHAR;
            }
        };
    }

    private CharReadBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private CharReadBuffer(final Connector connector, final URI uri) {
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
}
