package com.fineio.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/19
 */
public class LongBuffer extends BaseBuffer<LongBuffer.LongReadBuffer, LongBuffer.LongWriteBuffer> {
    public LongBuffer(Connector connector, URI uri, boolean syncWrite, Listener listener) {
        super(connector, uri, syncWrite, listener);
    }

    public LongBuffer(Connector connector, FileBlock block, int maxOffset, boolean syncWrite, Listener listener) {
        super(connector, block, maxOffset, syncWrite, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_LONG;
    }

    @Override
    public LongWriteBuffer asWrite() {
        return new LongBufferW();
    }

    @Override
    public LongReadBuffer asRead() {
        return new LongBufferR();
    }

    public interface LongReadBuffer extends BufferR {
        long get(int pos);
    }

    public interface LongWriteBuffer extends BufferW {
        void put(long value);

        void put(int pos, long value);
    }

    private class LongBufferR extends ReadBuffer implements LongReadBuffer {
        @Override
        public long get(int pos) {
            checkRead(pos);
            return MemoryUtils.getLong(getAddress(), pos);
        }
    }

    private class LongBufferW extends WriteBuffer implements LongWriteBuffer {
        @Override
        public void put(long value) {
            put(++writeCurrentPosition, value);
        }

        @Override
        public void put(int pos, long value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }
    }
}

