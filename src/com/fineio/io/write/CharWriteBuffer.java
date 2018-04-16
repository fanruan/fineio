package com.fineio.io.write;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.WriteModel;
import com.fineio.io.CharBuffer;
import com.fineio.io.pool.BufferPool;
import com.fineio.io.pool.PoolMode;
import com.fineio.io.read.CharReadBuffer;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public final class CharWriteBuffer extends WriteBuffer implements CharBuffer {

    public static final WriteModel MODEL = new WriteModel<CharBuffer>() {

        protected final CharWriteBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new CharWriteBuffer(connector, block, max_offset);
        }

        @Override
        public final CharWriteBuffer createBuffer(Connector connector, URI uri) {
            return new CharWriteBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };

    private CharWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private CharWriteBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final void put(char b) {
        put(++max_position, b);
    }

    /**
     * @param position 位置
     * @param b        值
     */
    public final void put(int position, char b) {
        ensureCapacity(position);
        MemoryUtils.put(address, position, b);
    }

    public final char get(int p) {
        checkIndex(p);
        return MemoryUtils.getChar(address, p);
    }

    @Override
    protected void registerForRead() {
        try {
            CharReadBuffer buffer = null;
            final Constructor<CharReadBuffer> constructor = CharReadBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
            constructorAccess(constructor);
            buffer = constructor.newInstance(bufferKey.getConnector(), bufferKey.getBlock(), current_max_offset);
            buffer.setAddress(address);
            buffer.setMaxSize(current_max_size);
            buffer.setAllocateSize(allocateSize);
            buffer.setLoad(true);
            BufferPool.getInstance(PoolMode.CHAR).registerFromBuffer(buffer, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
