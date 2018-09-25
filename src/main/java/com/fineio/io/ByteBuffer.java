package com.fineio.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * @author yee
 * @date 2018/9/19
 */
public class ByteBuffer extends BaseBuffer {
    public ByteBuffer(Connector connector, FileBlock block, int maxOffset, Listener listener) {
        super(connector, block, maxOffset, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_BYTE;
    }

    public byte get(int pos) {
        checkRead(pos);
        return MemoryUtils.getByte(memoryObject.getAddress(), pos);
    }

    public void put(byte value) {
        ensureCapacity(++writeCurrentPosition);
        MemoryUtils.put(address, writeCurrentPosition, value);
    }

}
