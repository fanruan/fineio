package com.fineio.v1.io.edit;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.v1.io.ByteBuffer;
import com.fineio.v1.io.file.EditModel;

import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final  class ByteEditBuffer extends  EditBuffer implements ByteBuffer {

    public static final EditModel MODEL = new EditModel<ByteBuffer>() {

        protected final ByteEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new ByteEditBuffer(connector, block, max_offset);
        }

        @Override
        public final ByteEditBuffer  createBuffer(Connector connector, URI uri) {
            return new ByteEditBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private ByteEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private ByteEditBuffer(Connector connector, URI uri) {
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