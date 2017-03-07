package com.fineio.io.write;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.io.LongBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/14.
 */
public  final class LongWriteBuffer extends WriteBuffer implements LongBuffer{

    public static final WriteModel MODEL = new WriteModel<LongBuffer>() {

        protected final LongWriteBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new LongWriteBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private LongWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final void put(long b) {
        put(++max_position, b);
    }
    /**
     *
     * @param position 位置
     * @param b 值
     */
    public final void put(int position, long b) {
        ensureCapacity(position);
        MemoryUtils.put(address, position, b);
    }

    public final long get(int p) {
        checkIndex(p);
        return MemoryUtils.getLong(address, p);
    }
}
