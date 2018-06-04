package com.fineio.v1.io.write;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.v1.io.ByteBuffer;
import com.fineio.v1.io.file.WriteModel;

import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final  class ByteWriteBuffer extends  WriteBuffer implements ByteBuffer {


    public static final WriteModel MODEL = new WriteModel<ByteBuffer>() {

        protected final ByteWriteBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new ByteWriteBuffer(connector, block, max_offset);
        }

        @Override
        public final ByteWriteBuffer createBuffer(Connector connector, URI uri) {
            return new ByteWriteBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };


    private ByteWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private ByteWriteBuffer(Connector connector, URI uri) {
        super(connector, uri);
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