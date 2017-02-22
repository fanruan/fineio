package com.fineio.io.edit;

import com.fineio.file.FileBlock;
import com.fineio.io.CharBuffer;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class CharEditBuffer extends EditBuffer implements CharBuffer {

    private CharEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final char get(int p) {
        checkIndex(p);
        return MemoryUtils.getChar(address, p);
    }

    /**
     *
     * @param position 位置
     * @param b 值
     */
    public  final  void put(int position, char b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private void judeChange(int position, char b) {
        if(!changed) {
            if(b != get(position)){
                changed = true;
            }
        }
    }
}
