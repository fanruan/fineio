package com.fineio.io.edit;

import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.io.IntBuffer;
import com.fineio.io.pool.BufferPool;
import com.fineio.io.pool.PoolMode;
import com.fineio.io.read.IntReadBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final class IntEditBuffer extends EditBuffer implements IntBuffer {

    public static final EditModel MODEL = new EditModel<IntBuffer>() {

        protected final IntEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new IntEditBuffer(connector, block, max_offset);
        }

        @Override
        public final IntEditBuffer createBuffer(Connector connector, URI uri) {
            return new IntEditBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private IntEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private IntEditBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final int get(int p) {
        checkIndex(p);
        return MemoryUtils.getInt(address, p);
    }


    public final void put(int b) {
        put(++max_position, b);
    }

    /**
     * @param position 位置
     * @param b        值
     */
    public final void put(int position, int b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private final void judeChange(int position, int b) {
        if (!changed) {
            if (b != get(position)) {
                changed = true;
            }
        }
    }

    @Override
    protected void registerForRead() {
        try {
            IntReadBuffer buffer = null;
            final Constructor<IntReadBuffer> constructor = IntReadBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
            constructorAccess(constructor);
            buffer = constructor.newInstance(bufferKey.getConnector(), bufferKey.getBlock(), current_max_offset);
            buffer.setAddress(address);
            buffer.setMaxSize(current_max_size);
            buffer.setAllocateSize(allocateSize);
            buffer.setLoad(true);
            BufferPool.getInstance(PoolMode.INT).registerFromBuffer(buffer, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
