package com.fineio.v1.io.edit;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.v1.io.CharBuffer;
import com.fineio.v1.io.file.EditModel;

import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final  class CharEditBuffer extends EditBuffer implements CharBuffer {

    public static final EditModel MODEL = new EditModel<CharBuffer>() {

        protected final CharEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new CharEditBuffer(connector, block, max_offset);
        }

        @Override
        public final CharEditBuffer createBuffer(Connector connector, URI uri) {
            return new CharEditBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private CharEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private CharEditBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final char get(int p) {
        checkIndex(p);
        return MemoryUtils.getChar(address, p);
    }


    public final void put(char b) {
        put(++max_position, b);
    }

    /**
     *
     * @param position 位置
     * @param b 值
     */
    public  final  void put(int position, char b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private void judeChange(int position, char b) {
        if(!changed) {
            if(b != get(position)){
                changed = true;
            }
        }
    }
}
