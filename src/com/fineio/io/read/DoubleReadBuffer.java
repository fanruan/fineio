package com.fineio.io.read;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.io.DoubleBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class DoubleReadBuffer extends ReadBuffer implements DoubleBuffer {


    public static final ReadModel MODEL = new ReadModel<DoubleBuffer>() {

        @Override
        protected final DoubleReadBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new DoubleReadBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };


    private DoubleReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final double get(int p) {
        checkIndex(p);
        return MemoryUtils.getDouble(address, p);
    }

}
