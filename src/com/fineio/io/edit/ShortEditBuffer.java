package com.fineio.io.edit;

import com.fineio.file.EditModel;
import com.fineio.file.FileBlock;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class ShortEditBuffer extends EditBuffer implements ShortBuffer {


    public static final EditModel MODEL = new EditModel<ShortBuffer>() {

        protected final ShortEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new ShortEditBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private ShortEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final short get(int p) {
        checkIndex(p);
        return MemoryUtils.getShort(address, p);
    }


    public final void put(short b) {
        ensureCapacity(max_position);
        MemoryUtils.put(address, max_position++, b);
    }

    /**
     *
     * @param position 位置
     * @param b 值
     */
    public  final  void put(int position, short b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private final void judeChange(int position, short b) {
        if(!changed) {
            if(b != get(position)){
                changed = true;
            }
        }
    }
}
