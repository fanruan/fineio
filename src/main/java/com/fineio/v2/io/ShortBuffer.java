package com.fineio.v2.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/19
 */
public class ShortBuffer extends BaseBuffer<ShortBuffer.ShortReadBuffer, ShortBuffer.ShortWriteBuffer> {
    public ShortBuffer(Connector connector, URI uri, boolean syncWrite, Listener listener) {
        super(connector, uri, syncWrite, listener);
    }

    public ShortBuffer(Connector connector, FileBlock block, int maxOffset, boolean syncWrite, Listener listener) {
        super(connector, block, maxOffset, syncWrite, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_SHORT;
    }

    @Override
    public ShortWriteBuffer asWrite() {
        return new ShortBufferW();
    }

    @Override
    public ShortReadBuffer asRead() {
        return new ShortBufferR();
    }

    public interface ShortReadBuffer extends BufferR {
        short get(int pos);
    }

    public interface ShortWriteBuffer extends BufferW {
        void put(short value);

        void put(int pos, short value);
    }

    private class ShortBufferR extends ReadBuffer implements ShortReadBuffer {
        @Override
        public short get(int pos) {
            return MemoryUtils.getShort(getReadAddress(pos), pos);
        }
    }

    private class ShortBufferW extends WriteBuffer implements ShortWriteBuffer {
        @Override
        public void put(short value) {
            put(++writeCurrentPosition, value);
        }

        @Override
        public void put(int pos, short value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }
    }
}

