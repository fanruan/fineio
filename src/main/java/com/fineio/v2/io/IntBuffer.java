package com.fineio.v2.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.v2.cache.CacheManager;
import com.fineio.v2.cache.pool.PoolMode;
import com.fineio.v2.io.edit.IntEditBuffer;
import com.fineio.v2.io.read.IntReadBuffer;
import com.fineio.v2.io.write.IntWriteBuffer;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/30
 */
public class IntBuffer extends AbstractBuffer<IntReadBuffer, IntWriteBuffer, IntEditBuffer> {
    static BufferModel MODE = new BufferModel<IntBuffer>() {

        @Override
        IntBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            IntBuffer buffer = CacheManager.getInstance().getBuffer(PoolMode.INT, block.getBlockURI());
            if (null == buffer) {
                buffer = new IntBuffer(connector, block, max_offset);
                CacheManager.getInstance().registerBuffer(PoolMode.INT, buffer);
            }
            return buffer;
        }

        @Override
        IntBuffer createBuffer(Connector connector, URI uri) {
            return new IntBuffer(connector, uri);
        }

        @Override
        byte offset() {
            return MemoryConstants.OFFSET_INT;
        }
    };

    protected IntBuffer(Connector connector, FileBlock block, int maxOffset) {
        super(connector, block, maxOffset);
    }

    protected IntBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    @Override
    protected void exitPool() {
        if (!directAccess) {
            manager.removeBuffer(PoolMode.INT, this);
        }
    }

    @Override
    protected IntReadBuffer createReadOnlyBuffer() {
        return new IntBufferR();
    }

    @Override
    protected IntWriteBuffer createWriteOnlyBuffer() {
        return new IntBufferW();
    }

    @Override
    protected IntEditBuffer createEditBuffer() {
        return new IntBufferE();
    }

    @Override
    public int getOffset() {
        return MemoryConstants.OFFSET_INT;
    }

    private final class IntBufferR extends InnerReadBuffer implements IntReadBuffer {

        @Override
        public int get(int pos) {
            lock.lock();
            try {
                check(pos);
                return MemoryUtils.getInt(address, pos);
            } finally {
                lock.unlock();
            }
        }

        @Override
        protected PoolMode poolMode() {
            return PoolMode.INT;
        }
    }

    private final class IntBufferW extends InnerWriteBuffer implements IntWriteBuffer {

        @Override
        public void put(int pos, int value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }

        @Override
        public void put(int value) {
            put(++maxPosition, value);
        }
    }

    @Deprecated
    private final class IntBufferE extends InnerEditBuffer implements IntEditBuffer {

        @Override
        public int get(int pos) {
            check(pos);
            return MemoryUtils.getInt(address, pos);
        }

        @Override
        public void put(int pos, int value) {
            ensureCapacity(pos);
            judeChange(pos, value);
            MemoryUtils.put(address, pos, value);
        }

        private void judeChange(int position, int b) {
            if (!changed) {
                if (b != get(position)) {
                    changed = true;
                }
            }
        }

        @Override
        public void put(int value) {
            put(++maxPosition, value);
        }
    }
}
