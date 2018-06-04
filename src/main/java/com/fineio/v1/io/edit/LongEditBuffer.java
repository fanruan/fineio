package com.fineio.v1.io.edit;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.v1.io.LongBuffer;
import com.fineio.v1.io.file.EditModel;

import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public  final class LongEditBuffer extends EditBuffer implements LongBuffer{

    public static final EditModel MODEL = new EditModel<LongBuffer>() {

        protected final LongEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new LongEditBuffer(connector, block, max_offset);
        }

        @Override
        public final LongEditBuffer  createBuffer(Connector connector, URI uri) {
            return new LongEditBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private LongEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private LongEditBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final long get(int p) {
        checkIndex(p);
        return MemoryUtils.getLong(address, p);
    }

    public final void put(long b) {
        put(++max_position, b);
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
