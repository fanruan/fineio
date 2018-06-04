package com.fineio.v1.io.read;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.v1.io.ByteBuffer;
import com.fineio.v1.io.file.ReadModel;

import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class ByteReadBuffer extends  ReadBuffer implements ByteBuffer {

    public static final ReadModel MODEL = new ReadModel<ByteBuffer>() {

        protected final ByteReadBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new ByteReadBuffer(connector, block, max_offset);
        }

        @Override
        public final ByteReadBuffer createBuffer(Connector connector, URI uri) {
            return new ByteReadBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private ByteReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private ByteReadBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final byte get(int p) {
        checkIndex(p);
        return MemoryUtils.getByte(address, p);
    }

}