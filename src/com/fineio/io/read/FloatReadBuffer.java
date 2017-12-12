package com.fineio.io.read;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.io.FloatBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class FloatReadBuffer extends ReadBuffer implements FloatBuffer {


    public static final ReadModel MODEL = new ReadModel<FloatBuffer>() {

        @Override
        protected final FloatBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new FloatReadBuffer(connector, block, max_offset);
        }

        @Override
        public final FloatBuffer createBuffer(Connector connector, URI uri) {
            return new FloatReadBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private FloatReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private FloatReadBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final float get(int p) {
        checkIndex(p);
        return MemoryUtils.getFloat(address, p);
    }
}
