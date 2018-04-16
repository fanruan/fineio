package com.fineio.io.write;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.io.LongBuffer;
import com.fineio.io.pool.BufferPool;
import com.fineio.io.pool.PoolMode;
import com.fineio.io.read.LongReadBuffer;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final class LongWriteBuffer extends WriteBuffer implements LongBuffer {

    public static final WriteModel MODEL = new WriteModel<LongBuffer>() {

        protected final LongWriteBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new LongWriteBuffer(connector, block, max_offset);
        }

        @Override
        public final LongWriteBuffer createBuffer(Connector connector, URI uri) {
            return new LongWriteBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private LongWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private LongWriteBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final void put(long b) {
        put(++max_position, b);
    }

    /**
     * @param position 位置
     * @param b        值
     */
    public final void put(int position, long b) {
        ensureCapacity(position);
        MemoryUtils.put(address, position, b);
    }

    public final long get(int p) {
        checkIndex(p);
        return MemoryUtils.getLong(address, p);
    }

    @Override
    protected void registerForRead() {
        try {
            LongReadBuffer buffer = null;
            final Constructor<LongReadBuffer> constructor = LongReadBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
            constructorAccess(constructor);
            buffer = constructor.newInstance(bufferKey.getConnector(), bufferKey.getBlock(), current_max_offset);
            buffer.setAddress(address);
            buffer.setMaxSize(current_max_size);
            buffer.setAllocateSize(allocateSize);
            buffer.setLoad(true);
            BufferPool.getInstance(PoolMode.LONG).registerFromBuffer(buffer, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
