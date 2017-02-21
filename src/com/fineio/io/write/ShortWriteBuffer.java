package com.fineio.io.write;

import com.fineio.file.FileBlock;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class ShortWriteBuffer extends WriteBuffer {
    public static final int OFFSET = MemoryConstants.OFFSET_SHORT;

    private ShortWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }
    /**
     *
     * @param position 位置
     * @param b 值
     */
    public final void put(int position, short b) {
        ensureCapacity(position);
        MemoryUtils.put(address, position, b);
    }
}
