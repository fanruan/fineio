package com.fineio.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * @author yee
 * @date 2018/9/19
 */
public class LongBuffer extends BaseBuffer<LongBuffer.LongReadBuffer, LongBuffer.LongWriteBuffer> {
    public LongBuffer(Connector connector, FileBlock block, int maxOffset, Listener listener) {
        super(connector, block, maxOffset, listener);
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
    }

    private class LongBufferR extends ReadBuffer implements LongReadBuffer {
        @Override
        public long get(int pos) {
            checkRead(pos);
            return MemoryUtils.getLong(memoryObject.getAddress(), pos);
        }
    }

    private class LongBufferW extends WriteBuffer implements LongWriteBuffer {
        @Override
        public void put(long value) {
            ensureCapacity(++writeCurrentPosition);
            MemoryUtils.put(address, writeCurrentPosition, value);
        }
    }
}

