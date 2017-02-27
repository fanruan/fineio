package com.fineio.io.write;

import com.fineio.file.FileBlock;
import com.fineio.file.WriteModel;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class FloatWriteBuffer extends WriteBuffer implements FloatBuffer {

    public static final WriteModel MODEL = new WriteModel<FloatBuffer>() {

        protected final FloatWriteBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new FloatWriteBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private FloatWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final void put(float b) {
        put(++max_position, b);
    }

    /**
     *
     * @param position 位置
     * @param b 值
     */
    public final void put(int position, float b) {
        ensureCapacity(position);
        MemoryUtils.put(address, position, b);
    }

    public final float get(int p) {
        checkIndex(p);
        return MemoryUtils.getFloat(address, p);
    }
}
