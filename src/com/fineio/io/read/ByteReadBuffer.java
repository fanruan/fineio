package com.fineio.io.read;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.io.ByteBuffer;
import com.fineio.io.pool.BufferPool;
import com.fineio.io.pool.PoolMode;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class ByteReadBuffer extends  ReadBuffer implements ByteBuffer {

    public static final ReadModel MODEL = new ReadModel<ByteBuffer>() {
        private BufferPool pool = BufferPool.getInstance(getPoolMode());
        public PoolMode getPoolMode() {
            return PoolMode.BYTE;
        }

        @Override
        protected ByteReadBuffer newBlockBuffer(Connector connector, FileBlock block, int max_offset) {
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