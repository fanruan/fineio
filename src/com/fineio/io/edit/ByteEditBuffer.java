package com.fineio.io.edit;

import com.fineio.file.FileBlock;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/9.
 */
public final  class ByteEditBuffer extends  EditBuffer {

    public static final int OFFSET = MemoryConstants.OFFSET_BYTE;

    private ByteEditBuffer(Connector connector, FileBlock block, int max_offset) {
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
    public  final  void put(int position, byte b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private void judeChange(int position, byte b) {
        if(!changed) {
            if(b != get(position)){
                changed = true;
            }
        }
    }

    public final byte get(int p) {
        checkIndex(p);
        return MemoryUtils.getByte(address, p);
    }

}