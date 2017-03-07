package com.fineio.io.write;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.io.DoubleBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;


/**
 * Created by daniel on 2017/2/14.
 */
public final  class DoubleWriteBuffer extends WriteBuffer implements DoubleBuffer {

    public static final WriteModel MODEL = new WriteModel<DoubleBuffer>() {

        protected final DoubleWriteBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new DoubleWriteBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private DoubleWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final void put(double b) {
        put(++max_position, b);
    }
    /**
     *
     * @param position 位置
     * @param b 值
     */
    public  final void put(int position, double b) {
        ensureCapacity(position);
        MemoryUtils.put(address, position, b);
    }

    public final double get(int p) {
        checkIndex(p);
        return MemoryUtils.getDouble(address, p);
    }
}
