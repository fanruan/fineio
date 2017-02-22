package com.fineio.io.edit;

import com.fineio.file.FileBlock;
import com.fineio.io.IntBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class IntEditBuffer extends EditBuffer implements IntBuffer {

    private IntEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final int get(int p) {
        checkIndex(p);
        return MemoryUtils.getInt(address, p);
    }

    /**
     *
     * @param position 位置
     * @param b 值
     */
    public  final  void put(int position, int b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private final void judeChange(int position, int b) {
        if(!changed) {
            if(b != get(position)){
                changed = true;
            }
        }
    }
}
