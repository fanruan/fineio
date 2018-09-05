package com.fineio.io;

import com.fineio.cache.CacheManager;
import com.fineio.cache.pool.PoolMode;
import com.fineio.io.edit.CharEditBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.read.CharReadBuffer;
import com.fineio.io.write.CharWriteBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/30
 */
public class CharBuffer extends AbstractBuffer<CharReadBuffer, CharWriteBuffer, CharEditBuffer> {

    static BufferModel MODE = new BufferModel<CharBuffer>() {
        @Override
        CharBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            CharBuffer buffer = CacheManager.getInstance().getBuffer(PoolMode.CHAR, block.getBlockURI());
            if (null == buffer) {
                buffer = new CharBuffer(connector, block, max_offset);
                CacheManager.getInstance().registerBuffer(PoolMode.CHAR, buffer);
            }
            return buffer;
        }

        @Override
        CharBuffer createBuffer(Connector connector, URI uri) {
            return new CharBuffer(connector, uri);
        }

        @Override
        byte offset() {
            return MemoryConstants.OFFSET_CHAR;
        }
    };

    protected CharBuffer(Connector connector, FileBlock block, int maxOffset) {
        super(connector, block, maxOffset);
    }

    protected CharBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    @Override
    protected void exitPool() {
        if (!directAccess) {
            manager.removeBuffer(PoolMode.CHAR, this);
        }
    }

    @Override
    protected CharReadBuffer createReadOnlyBuffer() {
        return new CharBufferR();
    }

    @Override
    protected CharWriteBuffer createWriteOnlyBuffer() {
        return new CharBufferW();
    }

    @Override
    protected CharEditBuffer createEditBuffer() {
        return new CharBufferE();
    }

    @Override
    public int getOffset() {
        return MemoryConstants.OFFSET_CHAR;
    }

    private final class CharBufferR extends InnerReadBuffer implements CharReadBuffer {

        @Override
        public char get(int pos) {
            lock.lock();
            try {
                check(pos);
                return MemoryUtils.getChar(address, pos);
            } finally {
                lock.unlock();
            }
        }

        @Override
        protected PoolMode poolMode() {
            return PoolMode.CHAR;
        }
    }

    private final class CharBufferW extends InnerWriteBuffer implements CharWriteBuffer {

        @Override
        public void put(int pos, char value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }

        @Override
        public void put(char value) {
            put(++maxPosition, value);
        }
    }

    @Deprecated
    private final class CharBufferE extends InnerEditBuffer implements CharEditBuffer {

        @Override
        public char get(int pos) {
            check(pos);
            return MemoryUtils.getChar(address, pos);
        }

        @Override
        public void put(int pos, char value) {
            ensureCapacity(pos);
            judeChange(pos, value);
            MemoryUtils.put(address, pos, value);
        }

        private void judeChange(int position, char b) {
            if (!changed) {
                if (b != get(position)) {
                    changed = true;
                }
            }
        }

        @Override
        public void put(char value) {
            put(++maxPosition, value);
        }
    }
}
