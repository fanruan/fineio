package com.fineio.io.read;

import com.fineio.io.IntBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

public final class IntReadBuffer extends ReadBuffer implements IntBuffer {
    public static final ReadModel MODEL;

    static {
        MODEL = new ReadModel<IntBuffer>() {
            @Override
            protected final IntReadBuffer createBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
                return new IntReadBuffer(connector, fileBlock, n, null);
            }

            @Override
            public final IntReadBuffer createBuffer(final Connector connector, final URI uri) {
                return new IntReadBuffer(connector, uri, null);
            }

            @Override
            protected final byte offset() {
                return 2;
            }
        };
    }

    private IntReadBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
    }

    private IntReadBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
    }

    @Override
    protected int getLengthOffset() {
        return 2;
    }

    @Override
    public final int get(final int n) {
        this.checkIndex(n);
        return MemoryUtils.getInt(this.address, n);
    }
}
