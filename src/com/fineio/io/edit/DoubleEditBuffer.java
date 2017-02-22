package com.fineio.io.edit;

import com.fineio.file.FileBlock;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class DoubleEditBuffer extends EditBuffer implements DoubleBuffer {

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
