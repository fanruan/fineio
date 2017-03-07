package com.fineio.io.write;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.io.ShortBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class ShortWriteBuffer extends WriteBuffer implements ShortBuffer{

    public static final WriteModel MODEL = new WriteModel<ShortBuffer>() {

        protected final ShortWriteBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new ShortWriteBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private ShortWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final void put(short b) {
        put(++max_position, b);
    }
    /**
     *
     * @param position 位置
     * @param b 值
     */
    public final void put(int position, short b) {
        ensureCapacity(position);
        MemoryUtils.put(address, position, b);
    }

    public final short get(int p) {
        checkIndex(p);
        return MemoryUtils.getShort(address, p);
    }
}
