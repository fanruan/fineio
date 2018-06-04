package com.fineio.io.edit;

import com.fineio.io.file.EditModel;
import com.fineio.io.file.FileBlock;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.pool.BufferPool;
import com.fineio.io.pool.PoolMode;
import com.fineio.io.read.DoubleReadBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final class DoubleEditBuffer extends EditBuffer implements DoubleBuffer {

    public static final EditModel MODEL = new EditModel<DoubleBuffer>() {

        protected final DoubleEditBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new DoubleEditBuffer(connector, block, max_offset);
        }

        public final DoubleEditBuffer createBuffer(Connector connector, URI uri) {
            return new DoubleEditBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private DoubleEditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private DoubleEditBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public final double get(int p) {
        checkIndex(p);
        return MemoryUtils.getDouble(address, p);
    }

    public final void put(double b) {
        put(++max_position, b);
    }

    /**
     * @param position 位置
     * @param b        值
     */
    public final void put(int position, double b) {
        ensureCapacity(position);
        judeChange(position, b);
        MemoryUtils.put(address, position, b);
    }

    private final void judeChange(int position, double b) {
        if (!changed) {
            if (Double.compare(b, get(position)) != 0) {
                changed = true;
            }
        }
    }

    @Override
    protected void registerForRead() {
        try {
            DoubleReadBuffer buffer = null;
            final Constructor<DoubleReadBuffer> constructor = DoubleReadBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
            constructorAccess(constructor);
            buffer = constructor.newInstance(bufferKey.getConnector(), bufferKey.getBlock(), current_max_offset);
            buffer.setAddress(address);
            buffer.setMaxSize(current_max_size);
            buffer.setAllocateSize(allocateSize);
            buffer.setLoad(true);
            BufferPool.getInstance(PoolMode.DOUBLE).registerFromBuffer(buffer, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
