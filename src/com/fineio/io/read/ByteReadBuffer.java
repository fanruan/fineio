package com.fineio.io.read;

import com.fineio.file.FileBlock;
import com.fineio.file.ReadModel;
import com.fineio.io.ByteBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/9.
 */
public final class ByteReadBuffer extends  ReadBuffer implements ByteBuffer {

    public static final ReadModel MODEL = new ReadModel<ByteBuffer>() {

        protected final ByteReadBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new ByteReadBuffer(connector, block, max_offset);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private ByteReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final byte get(int p) {
        checkIndex(p);
        return MemoryUtils.getByte(address, p);
    }

}