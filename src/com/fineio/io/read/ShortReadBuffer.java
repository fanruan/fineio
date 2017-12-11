package com.fineio.io.read;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.io.ShortBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class ShortReadBuffer extends ReadBuffer implements ShortBuffer{


    public static final ReadModel MODEL = new ReadModel<ShortBuffer>() {

        @Override
        protected final ShortBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new ShortReadBuffer(connector, block, max_offset);
        }

        @Override
        public final ShortBuffer createBuffer(Connector connector, URI uri) {
            return new ShortReadBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };


    private ShortReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    public ShortReadBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final short get(int p) {
        checkIndex(p);
        return MemoryUtils.getShort(address, p);
    }
}
