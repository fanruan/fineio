package com.fineio.v2.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.v2.cache.CacheManager;
import com.fineio.v2.cache.pool.PoolMode;
import com.fineio.v2.io.edit.ShortEditBuffer;
import com.fineio.v2.io.read.ShortReadBuffer;
import com.fineio.v2.io.write.ShortWriteBuffer;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/30
 */
public class ShortBuffer extends AbstractBuffer<ShortReadBuffer, ShortWriteBuffer, ShortEditBuffer> {

    static BufferModel MODE = new BufferModel<ShortBuffer>() {

        @Override
        ShortBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            ShortBuffer buffer = CacheManager.getInstance().getBuffer(PoolMode.SHORT, block.getBlockURI());
            if (null == buffer) {
                buffer = new ShortBuffer(connector, block, max_offset);
                CacheManager.getInstance().registerBuffer(PoolMode.SHORT, buffer);
            }
            return buffer;
        }

        @Override
        ShortBuffer createBuffer(Connector connector, URI uri) {
            return new ShortBuffer(connector, uri);
        }

        @Override
        byte offset() {
            return MemoryConstants.OFFSET_SHORT;
        }
    };

    protected ShortBuffer(Connector connector, FileBlock block, int maxOffset) {
        super(connector, block, maxOffset);
    }

    protected ShortBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    @Override
    protected void exitPool() {
        if (!directAccess) {
            manager.removeBuffer(PoolMode.SHORT, this);
        }
    }

    @Override
    protected ShortReadBuffer createReadOnlyBuffer() {
        return new ShortBufferR();
    }

    @Override
    protected ShortWriteBuffer createWriteOnlyBuffer() {
        return new ShortBufferW();
    }

    @Override
    protected ShortEditBuffer createEditBuffer() {
        return new ShortBufferE();
    }

    @Override
    public int getOffset() {
        return MemoryConstants.OFFSET_SHORT;
    }

    private final class ShortBufferR extends InnerReadBuffer implements ShortReadBuffer {

        @Override
        public short get(int pos) {
            lock.lock();
            try {
                check(pos);
                return MemoryUtils.getShort(address, pos);
            } finally {
                lock.unlock();
            }
        }

        @Override
        protected PoolMode poolMode() {
            return PoolMode.SHORT;
        }
    }

    private final class ShortBufferW extends InnerWriteBuffer implements ShortWriteBuffer {

        @Override
        public void put(int pos, short value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }

        @Override
        public void put(short value) {
            put(++maxPosition, value);
        }
    }

    @Deprecated
    private final class ShortBufferE extends InnerEditBuffer implements ShortEditBuffer {

        @Override
        public short get(int pos) {
            check(pos);
            return MemoryUtils.getShort(address, pos);
        }

        @Override
        public void put(int pos, short value) {
            ensureCapacity(pos);
            judeChange(pos, value);
            MemoryUtils.put(address, pos, value);
        }

        private void judeChange(int position, short b) {
            if (!changed) {
                if (b != get(position)) {
                    changed = true;
                }
            }
        }

        @Override
        public void put(short value) {
            put(++maxPosition, value);
        }
    }
}
