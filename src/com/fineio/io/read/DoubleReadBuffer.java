package com.fineio.io.read;

import com.fineio.file.FileBlock;
import com.fineio.io.DoubleBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class DoubleReadBuffer extends ReadBuffer implements DoubleBuffer {


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
