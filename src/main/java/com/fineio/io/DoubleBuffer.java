package com.fineio.io;

import com.fineio.cache.CacheManager;
import com.fineio.cache.pool.PoolMode;
import com.fineio.io.edit.DoubleEditBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.read.DoubleReadBuffer;
import com.fineio.io.write.DoubleWriteBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/30
 */
public class DoubleBuffer extends AbstractBuffer<DoubleReadBuffer, DoubleWriteBuffer, DoubleEditBuffer> {

    static BufferModel MODE = new BufferModel<DoubleBuffer>() {

        @Override
        DoubleBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            DoubleBuffer buffer = CacheManager.getInstance().getBuffer(PoolMode.DOUBLE, block.getBlockURI());
            if (null == buffer) {
                buffer = new DoubleBuffer(connector, block, max_offset);
                CacheManager.getInstance().registerBuffer(PoolMode.DOUBLE, buffer);
            }
            return buffer;
        }

        @Override
        DoubleBuffer createBuffer(Connector connector, URI uri) {
            return new DoubleBuffer(connector, uri);
        }

        @Override
        byte offset() {
            return MemoryConstants.OFFSET_DOUBLE;
        }
    };

    protected DoubleBuffer(Connector connector, FileBlock block, int maxOffset) {
        super(connector, block, maxOffset);
    }

    protected DoubleBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    @Override
    protected void exitPool() {
        manager.removeBuffer(PoolMode.DOUBLE, this);
    }

    @Override
    protected DoubleReadBuffer createReadOnlyBuffer() {
        return new DoubleBufferR();
    }

    @Override
    protected DoubleWriteBuffer createWriteOnlyBuffer() {
        return new DoubleBufferW();
    }

    @Override
    protected DoubleEditBuffer createEditBuffer() {
        return new DoubleBufferE();
    }

    @Override
    public int getOffset() {
        return MemoryConstants.OFFSET_DOUBLE;
    }

    private final class DoubleBufferR extends InnerReadBuffer implements DoubleReadBuffer {

        @Override
        public double get(int pos) {
            check(pos);
            return MemoryUtils.getDouble(address, pos);
        }
    }

    private final class DoubleBufferW extends InnerWriteBuffer implements DoubleWriteBuffer {

        @Override
        public void put(int pos, double value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }

        @Override
        public void put(double value) {
            put(++maxPosition, value);
        }
    }

    private final class DoubleBufferE extends InnerEditBuffer implements DoubleEditBuffer {

        @Override
        public double get(int pos) {
            check(pos);
            return MemoryUtils.getDouble(address, pos);
        }

        @Override
        public void put(int pos, double value) {
            ensureCapacity(pos);
            judeChange(pos, value);
            MemoryUtils.put(address, pos, value);
        }

        private void judeChange(int position, double b) {
            if (!changed) {
                if (0 != Double.compare(b, get(position))) {
                    changed = true;
                }
            }
        }

        @Override
        public void put(double value) {
            put(++maxPosition, value);
        }
    }
}
