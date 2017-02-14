package com.fineio.io.read;

import com.fineio.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public class LongReadBuffer extends ByteReadBuffer {
    public LongReadBuffer(Connector connector, FileBlock block) {
        super(connector, block);
    }

    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_LONG;
    }

    public final long getLong(int p) {
        checkIndex(p);
        return MemoryUtils.getLong(address, p);
    }
}
