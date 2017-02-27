package com.fineio.io.write;

import com.fineio.file.FileBlock;
import com.fineio.file.WriteModel;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class IntWriteBuffer extends WriteBuffer implements IntBuffer{

    public static final WriteModel MODEL = new WriteModel<IntBuffer>() {

        protected final IntWriteBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new IntWriteBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private IntWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final void put(int b) {
        put(++max_position, b);
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

    public final int get(int p) {
        checkIndex(p);
        return MemoryUtils.getInt(address, p);
    }
}
