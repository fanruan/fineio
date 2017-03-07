package com.fineio.io.edit;

import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.io.DoubleBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class DoubleEditBuffer extends EditBuffer implements DoubleBuffer {

    public static final EditModel MODEL = new EditModel<DoubleBuffer>() {

        protected final DoubleEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new DoubleEditBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private DoubleEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final double get(int p) {
        checkIndex(p);
        return MemoryUtils.getDouble(address, p);
    }

    public final void put(double b) {
        put(++max_position, b);
    }

    /**
     *
     * @param position 位置
     * @param b 值
     */
    public  final  void put(int position, double b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private final void judeChange(int position, double b) {
        if(!changed) {
            if(Double.compare(b, get(position)) != 0){
                changed = true;
            }
        }
    }
}
