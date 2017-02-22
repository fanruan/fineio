package com.fineio.io.write;

import com.fineio.file.FileBlock;
import com.fineio.io.IntBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class IntWriteBuffer extends WriteBuffer implements IntBuffer{

    private IntWriteBuffer(Connector connector, FileBlock block, int max_offset) {
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
    public final void put(int position, int b) {
        ensureCapacity(position);
        MemoryUtils.put(address, position, b);
    }
}
