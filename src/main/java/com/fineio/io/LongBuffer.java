package com.fineio.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * @author yee
 * @date 2018/9/19
 */
public class LongBuffer extends BaseBuffer {
    public LongBuffer(Connector connector, FileBlock block, int maxOffset, Listener listener) {
        super(connector, block, maxOffset, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_LONG;
    }

    public long get(int pos) {
        checkRead(pos);
        return MemoryUtils.getLong(memoryObject.getAddress(), pos);
    }

    public void put(long value) {
        ensureCapacity(++writeCurrentPosition);
        MemoryUtils.put(address, writeCurrentPosition, value);
    }
}
