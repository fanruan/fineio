package com.fineio.io.read;

import com.fineio.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public class CharReadBuffer extends ReadBuffer {

    public static final int OFFSET = MemoryConstants.OFFSET_CHAR;

    private CharReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final char get(int p) {
        checkIndex(p);
        return MemoryUtils.getChar(address, p);
    }
}
