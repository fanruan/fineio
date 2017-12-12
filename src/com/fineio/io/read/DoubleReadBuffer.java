package com.fineio.io.read;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.io.DoubleBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class DoubleReadBuffer extends ReadBuffer implements DoubleBuffer {


    public static final ReadModel MODEL = new ReadModel<DoubleBuffer>() {

        @Override
        protected final DoubleBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new DoubleReadBuffer(connector, block, max_offset);
        }

        @Override
        public final DoubleBuffer  createBuffer(Connector connector, URI uri) {
            return new DoubleReadBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };


    private DoubleReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private DoubleReadBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final double get(int p) {
        checkIndex(p);
        return MemoryUtils.getDouble(address, p);
    }

}
