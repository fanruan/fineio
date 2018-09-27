package com.fineio.io;


import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * @author yee
 * @date 2018/9/19
 */
public class IntBuffer extends BaseBuffer<IntBuffer.IntReadBuffer, IntBuffer.IntWriteBuffer> {
    public IntBuffer(Connector connector, FileBlock block, int maxOffset, Listener listener) {
        super(connector, block, maxOffset, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_INT;
    }

    @Override
    public IntWriteBuffer asWrite() {
        return new IntBufferW();
    }

    @Override
    public IntReadBuffer asRead() {
        return new IntBufferR();
    }

    public interface IntReadBuffer extends BufferR {
        int get(int pos);
    }

    public interface IntWriteBuffer extends BufferW {
        void put(int value);
    }

    private class IntBufferR extends ReadBuffer implements IntReadBuffer {
        @Override
        public int get(int pos) {
            checkRead(pos);
            return MemoryUtils.getInt(memoryObject.getAddress(), pos);
        }
    }

    private class IntBufferW extends WriteBuffer implements IntWriteBuffer {
        @Override
        public void put(int value) {
            ensureCapacity(++writeCurrentPosition);
            MemoryUtils.put(address, writeCurrentPosition, value);
        }
    }
}

