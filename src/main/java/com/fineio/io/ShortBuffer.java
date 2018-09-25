package com.fineio.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * @author yee
 * @date 2018/9/19
 */
public class ShortBuffer extends BaseBuffer {
    public ShortBuffer(Connector connector, FileBlock block, int maxOffset, Listener listener) {
        super(connector, block, maxOffset, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_SHORT;
    }

    public short get(int pos) {
        checkRead(pos);
        return MemoryUtils.getShort(memoryObject.getAddress(), pos);
    }

    public void put(short value) {
        ensureCapacity(++writeCurrentPosition);
        MemoryUtils.put(address, writeCurrentPosition, value);
    }

}
