package com.fineio.io.write;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.io.ByteBuffer;
import com.fineio.io.pool.BufferPool;
import com.fineio.io.pool.PoolMode;
import com.fineio.io.read.ByteReadBuffer;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class ByteWriteBuffer extends WriteBuffer implements ByteBuffer {


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
     * @param position 位置
     * @param b        值
     */
    public final void put(int position, byte b) {
        ensureCapacity(position);
        MemoryUtils.put(address, position, b);
    }

    public final byte get(int p) {
        checkIndex(p);
        return MemoryUtils.getByte(address, p);
    }

    @Override
    protected void registerForRead() {
        try {
            ByteReadBuffer buffer = null;
            final Constructor<ByteReadBuffer> constructor = ByteReadBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
            constructorAccess(constructor);
            buffer = constructor.newInstance(bufferKey.getConnector(), bufferKey.getBlock(), current_max_offset);
            buffer.setAddress(address);
            buffer.setMaxSize(current_max_size);
            buffer.setAllocateSize(allocateSize);
            buffer.setLoad(true);
            BufferPool.getInstance(PoolMode.BYTE).registerFromBuffer(buffer, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}