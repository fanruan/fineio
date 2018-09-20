package com.fineio.v2.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.v2.cache.CacheManager;
import com.fineio.v2.cache.pool.PoolMode;
import com.fineio.v2.io.edit.ByteEditBuffer;
import com.fineio.v2.io.read.ByteReadBuffer;
import com.fineio.v2.io.write.ByteWriteBuffer;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/30
 */
public class ByteBuffer extends AbstractBuffer<ByteReadBuffer, ByteWriteBuffer, ByteEditBuffer> {

    static BufferModel MODE = new BufferModel<ByteBuffer>() {

        @Override
        ByteBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            ByteBuffer buffer = CacheManager.getInstance().getBuffer(PoolMode.BYTE, block.getBlockURI());
            if (null == buffer) {
                buffer = new ByteBuffer(connector, block, max_offset);
                CacheManager.getInstance().registerBuffer(PoolMode.BYTE, buffer);
            }
            return buffer;
        }

        @Override
        ByteBuffer createBuffer(Connector connector, URI uri) {
            return new ByteBuffer(connector, uri);
        }

        @Override
        byte offset() {
            return MemoryConstants.OFFSET_BYTE;
        }
    };

    protected ByteBuffer(Connector connector, FileBlock block, int maxOffset) {
        super(connector, block, maxOffset);
    }

    protected ByteBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    @Override
    protected void exitPool() {
        if (!directAccess) {
            manager.removeBuffer(PoolMode.BYTE, this);
        }
    }

    @Override
    protected ByteReadBuffer createReadOnlyBuffer() {
        return new ByteBufferR();
    }

    @Override
    protected ByteWriteBuffer createWriteOnlyBuffer() {
        return new ByteBufferW();
    }

    @Override
    protected ByteEditBuffer createEditBuffer() {
        return new ByteBufferE();
    }

    @Override
    public int getOffset() {
        return MemoryConstants.OFFSET_BYTE;
    }

    private final class ByteBufferR extends InnerReadBuffer implements ByteReadBuffer {

        @Override
        public byte get(int pos) {
            lock.lock();
            try {
                check(pos);
                return MemoryUtils.getByte(address, pos);
            } finally {
                lock.unlock();
            }
        }

        @Override
        protected PoolMode poolMode() {
            return PoolMode.BYTE;
        }
    }

    private final class ByteBufferW extends InnerWriteBuffer implements ByteWriteBuffer {

        @Override
        public void put(int pos, byte value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }

        @Override
        public void put(byte value) {
            put(++maxPosition, value);
        }
    }

    @Deprecated
    private final class ByteBufferE extends InnerEditBuffer implements ByteEditBuffer {

        @Override
        public byte get(int pos) {
            check(pos);
            return MemoryUtils.getByte(address, pos);
        }

        @Override
        public void put(int pos, byte value) {
            ensureCapacity(pos);
            judeChange(pos, value);
            MemoryUtils.put(address, pos, value);
        }

        private void judeChange(int position, byte b) {
            if (!changed) {
                if (b != get(position)) {
                    changed = true;
                }
            }
        }

        @Override
        public void put(byte value) {
            put(++maxPosition, value);
        }
    }
}
