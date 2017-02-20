package com.fineio.io.write;

import com.fineio.file.FileBlock;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public class FloatWriteBuffer extends WriteBuffer {

    public static final int OFFSET = MemoryConstants.OFFSET_FLOAT;

    private FloatWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final void put(float b) {
        ensureCapacity();
        MemoryUtils.put(address, position++, b);
    }
}
