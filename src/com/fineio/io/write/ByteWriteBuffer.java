package com.fineio.io.write;

import com.fineio.file.FileBlock;
import com.fineio.file.ReadModel;
import com.fineio.file.WriteModel;
import com.fineio.io.ByteBuffer;
import com.fineio.io.read.ByteReadBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/9.
 */
public final  class ByteWriteBuffer extends  WriteBuffer implements ByteBuffer {


    public static final WriteModel MODEL = new WriteModel<ByteBuffer>() {

        protected final ByteWriteBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new ByteWriteBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };


    private ByteWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }



    public final void put(byte b) {
        put(++max_position, b);
    }
    /**
     *
     * @param position 位置
     * @param b 值
     */
    public  final  void put(int position, byte b) {
        ensureCapacity(position);
        MemoryUtils.put(address, position, b);
    }

    public final byte get(int p) {
        checkIndex(p);
        return MemoryUtils.getByte(address, p);
    }

}