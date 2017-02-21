package com.fineio.io.edit;

import com.fineio.file.FileBlock;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public  final class LongEditBuffer extends EditBuffer {
    public static final int OFFSET = MemoryConstants.OFFSET_LONG;

    private LongEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final long get(int p) {
        checkIndex(p);
        return MemoryUtils.getLong(address, p);
    }

    /**
     *
     * @param position 位置
     * @param b 值
     */
    public  final  void put(int position, long b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private final void judeChange(int position, long b) {
        if(!changed) {
            if(b != get(position)){
                changed = true;
            }
        }
    }
}
