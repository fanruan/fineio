package com.fineio.io.read;

import com.fineio.io.CharBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class CharReadBuffer extends ReadBuffer implements CharBuffer {
    public static final ReadModel MODEL;

    static {
        MODEL = new ReadModel<CharBuffer>() {
            @Override
            protected final CharReadBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new CharReadBuffer(connector, fileBlock, n, null);
            }

            @Override
            public final CharReadBuffer createBuffer(final Connector connector, final URI uri) {
                return new CharReadBuffer(connector, uri, null);
            }

            @Override
            protected final byte offset() {
                return 1;
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
        return 1;
    }

    @Override
    public final char get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getChar(this.address, n);
    }
}
