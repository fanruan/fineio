package com.fineio.io.edit;

import com.fineio.file.EditModel;
import com.fineio.file.FileBlock;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public  final class LongEditBuffer extends EditBuffer implements LongBuffer{

    public static final EditModel MODEL = new EditModel<LongBuffer>() {

        protected final LongEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new LongEditBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

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

    public final void put(long b) {
        ensureCapacity(max_position);
        MemoryUtils.put(address, max_position++, b);
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
