package com.fineio.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * @author yee
 * @date 2018/9/19
 */
public class DoubleBuffer extends BaseBuffer {
    public DoubleBuffer(Connector connector, FileBlock block, int maxOffset, Listener listener) {
        super(connector, block, maxOffset, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_DOUBLE;
    }

    public double get(int pos) {
        checkRead(pos);
        return MemoryUtils.getDouble(memoryObject.getAddress(), pos);
    }

    public void put(double value) {
        ensureCapacity(++writeCurrentPosition);
        MemoryUtils.put(address, writeCurrentPosition, value);
    }
}
