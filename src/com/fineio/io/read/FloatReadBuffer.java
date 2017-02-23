package com.fineio.io.read;

import com.fineio.file.FileBlock;
import com.fineio.file.ReadModel;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class FloatReadBuffer extends ReadBuffer implements FloatBuffer {


    public static final ReadModel MODEL = new ReadModel<FloatBuffer>() {

        @Override
        protected final FloatReadBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new FloatReadBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private FloatReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final float get(int p) {
        checkIndex(p);
        return MemoryUtils.getFloat(address, p);
    }
}
