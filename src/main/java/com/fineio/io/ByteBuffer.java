package com.fineio.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * @author yee
 * @date 2018/9/19
 */
public class ByteBuffer extends BaseBuffer<ByteBuffer.ByteReadBuffer, ByteBuffer.ByteWriteBuffer> {
    public ByteBuffer(Connector connector, FileBlock block, int maxOffset, Listener listener) {
        super(connector, block, maxOffset, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_BYTE;
    }

    @Override
    public ByteWriteBuffer asWrite() {
        return new ByteBufferW();
    }

    @Override
    public ByteReadBuffer asRead() {
        return new ByteBufferR();
    }

    public interface ByteReadBuffer extends BufferR {
        byte get(int pos);
    }

    public interface ByteWriteBuffer extends BufferW {
        void put(byte value);
    }

    private class ByteBufferR extends ReadBuffer implements ByteReadBuffer {
        @Override
        public byte get(int pos) {
            checkRead(pos);
            return MemoryUtils.getByte(readAddress, pos);
        }
    }

    private class ByteBufferW extends WriteBuffer implements ByteWriteBuffer {
        @Override
        public void put(byte value) {
            ensureCapacity(++writeCurrentPosition);
            MemoryUtils.put(address, writeCurrentPosition, value);
        }
    }
}

