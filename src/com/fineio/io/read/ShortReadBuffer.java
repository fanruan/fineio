package com.fineio.io.read;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.io.ShortBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class ShortReadBuffer extends ReadBuffer implements ShortBuffer{


    public static final ReadModel MODEL = new ReadModel<ShortBuffer>() {

        @Override
        protected final ShortReadBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new ShortReadBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };


    private ShortReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final short get(int p) {
        checkIndex(p);
        return MemoryUtils.getShort(address, p);
    }
}
