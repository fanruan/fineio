package com.fineio.io;

import com.fineio.cache.CacheManager;
import com.fineio.cache.pool.PoolMode;
import com.fineio.io.edit.LongEditBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.read.LongReadBuffer;
import com.fineio.io.write.LongWriteBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/30
 */
public class LongBuffer extends AbstractBuffer<LongReadBuffer, LongWriteBuffer, LongEditBuffer> {

    static BufferModel MODE = new BufferModel<LongBuffer>() {

        @Override
        LongBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            LongBuffer buffer = CacheManager.getInstance().getBuffer(PoolMode.LONG, block.getBlockURI());
            if (null == buffer) {
                buffer = new LongBuffer(connector, block, max_offset);
                CacheManager.getInstance().registerBuffer(PoolMode.LONG, buffer);
            }
            return buffer;
        }

        @Override
        LongBuffer createBuffer(Connector connector, URI uri) {
            return new LongBuffer(connector, uri);
        }

        @Override
        byte offset() {
            return MemoryConstants.OFFSET_LONG;
        }
    };

    protected LongBuffer(Connector connector, FileBlock block, int maxOffset) {
        super(connector, block, maxOffset);
    }

    protected LongBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    @Override
    protected void exitPool() {
        manager.removeBuffer(PoolMode.LONG, this);
    }

    @Override
    protected LongReadBuffer createReadOnlyBuffer() {
        return new LongBufferR();
    }

    @Override
    protected LongWriteBuffer createWriteOnlyBuffer() {
        return new LongBufferW();
    }

    @Override
    protected LongEditBuffer createEditBuffer() {
        return new LongBufferE();
    }

    @Override
    public int getOffset() {
        return MemoryConstants.OFFSET_LONG;
    }

    private final class LongBufferR extends InnerReadBuffer implements LongReadBuffer {

        @Override
        public long get(int pos) {
            check(pos);
            return MemoryUtils.getLong(address, pos);
        }

        @Override
        protected PoolMode poolMode() {
            return PoolMode.LONG;
        }
    }

    private final class LongBufferW extends InnerWriteBuffer implements LongWriteBuffer {

        @Override
        public void put(int pos, long value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }

        @Override
        public void put(long value) {
            put(++maxPosition, value);
        }
    }

    @Deprecated
    private final class LongBufferE extends InnerEditBuffer implements LongEditBuffer {

        @Override
        public long get(int pos) {
            check(pos);
            return MemoryUtils.getLong(address, pos);
        }

        @Override
        public void put(int pos, long value) {
            ensureCapacity(pos);
            judeChange(pos, value);
            MemoryUtils.put(address, pos, value);
        }

        private void judeChange(int position, long b) {
            if (!changed) {
                if (b != get(position)) {
                    changed = true;
                }
            }
        }

        @Override
        public void put(long value) {
            put(++maxPosition, value);
        }
    }
}
