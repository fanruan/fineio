package com.fineio.v1.io.edit;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.v1.io.ShortBuffer;
import com.fineio.v1.io.file.EditModel;

import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class ShortEditBuffer extends EditBuffer implements ShortBuffer {


    public static final EditModel MODEL = new EditModel<ShortBuffer>() {

        protected final ShortEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new ShortEditBuffer(connector, block, max_offset);
        }

        @Override
        public final ShortEditBuffer  createBuffer(Connector connector, URI uri) {
            return new ShortEditBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private ShortEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private ShortEditBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final short get(int p) {
        checkIndex(p);
        return MemoryUtils.getShort(address, p);
    }


    public final void put(short b) {
        put(++max_position, b);
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
