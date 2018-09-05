package com.fineio.io;

import com.fineio.cache.CacheManager;
import com.fineio.cache.pool.PoolMode;
import com.fineio.io.edit.FloatEditBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.read.FloatReadBuffer;
import com.fineio.io.write.FloatWriteBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/30
 */
public class FloatBuffer extends AbstractBuffer<FloatReadBuffer, FloatWriteBuffer, FloatEditBuffer> {

    static BufferModel MODE = new BufferModel<FloatBuffer>() {
        @Override
        FloatBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            FloatBuffer buffer = CacheManager.getInstance().getBuffer(PoolMode.FLOAT, block.getBlockURI());
            if (null == buffer) {
                buffer = new FloatBuffer(connector, block, max_offset);
                CacheManager.getInstance().registerBuffer(PoolMode.FLOAT, buffer);
            }
            return buffer;
        }

        @Override
        FloatBuffer createBuffer(Connector connector, URI uri) {
            return new FloatBuffer(connector, uri);
        }

        @Override
        byte offset() {
            return MemoryConstants.OFFSET_FLOAT;
        }
    };

    protected FloatBuffer(Connector connector, FileBlock block, int maxOffset) {
        super(connector, block, maxOffset);
    }

    protected FloatBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    @Override
    protected void exitPool() {
        if (!directAccess) {
            manager.removeBuffer(PoolMode.FLOAT, this);
        }
    }

    @Override
    protected FloatReadBuffer createReadOnlyBuffer() {
        return new FloatBufferR();
    }

    @Override
    protected FloatWriteBuffer createWriteOnlyBuffer() {
        return new FloatBufferW();
    }

    @Override
    protected FloatEditBuffer createEditBuffer() {
        return new FloatBufferE();
    }

    @Override
    public int getOffset() {
        return MemoryConstants.OFFSET_FLOAT;
    }

    private final class FloatBufferR extends InnerReadBuffer implements FloatReadBuffer {

        @Override
        public float get(int pos) {
            lock.lock();
            try {
                check(pos);
                return MemoryUtils.getFloat(address, pos);
            } finally {
                lock.unlock();
            }
        }

        @Override
        protected PoolMode poolMode() {
            return PoolMode.FLOAT;
        }
    }

    private final class FloatBufferW extends InnerWriteBuffer implements FloatWriteBuffer {

        @Override
        public void put(int pos, float value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }

        @Override
        public void put(float value) {
            put(++maxPosition, value);
        }
    }

    @Deprecated
    private final class FloatBufferE extends InnerEditBuffer implements FloatEditBuffer {

        @Override
        public float get(int pos) {
            check(pos);
            return MemoryUtils.getFloat(address, pos);
        }

        @Override
        public void put(int pos, float value) {
            ensureCapacity(pos);
            judeChange(pos, value);
            MemoryUtils.put(address, pos, value);
        }

        private void judeChange(int position, float b) {
            if (!changed) {
                if (0 != Float.compare(b, get(position))) {
                    changed = true;
                }
            }
        }

        @Override
        public void put(float value) {
            put(++maxPosition, value);
        }
    }
}
