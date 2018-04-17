package com.fineio.io.edit;

import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.io.LongBuffer;
import com.fineio.io.pool.BufferPool;
import com.fineio.io.pool.PoolMode;
import com.fineio.io.read.LongReadBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final class LongEditBuffer extends EditBuffer implements LongBuffer {

    public static final EditModel MODEL = new EditModel<LongBuffer>() {

        protected final LongEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new LongEditBuffer(connector, block, max_offset);
        }

        @Override
        public final LongEditBuffer createBuffer(Connector connector, URI uri) {
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
     * @param position 位置
     * @param b        值
     */
    public final void put(int position, long b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private final void judeChange(int position, long b) {
        if (!changed) {
            if (b != get(position)) {
                changed = true;
            }
        }
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
            BufferPool.getInstance(PoolMode.LONG).registerFromBuffer(buffer, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
