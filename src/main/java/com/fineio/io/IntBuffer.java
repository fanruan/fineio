package com.fineio.io;


import com.fineio.accessor.buffer.IntBuf;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/19
 */
public class IntBuffer extends BaseBuffer<IntBuffer.IntReadBuffer, IntBuffer.IntWriteBuffer> implements IntBuf {
    public IntBuffer(Connector connector, URI uri, boolean syncWrite, Listener listener) {
        super(connector, uri, syncWrite, listener);
    }

    public IntBuffer(Connector connector, FileBlock block, int maxOffset, boolean syncWrite, Listener listener) {
        super(connector, block, maxOffset, syncWrite, listener);
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

        void put(int pos, int value);
    }

    private class IntBufferR extends ReadBuffer implements IntReadBuffer {
        @Override
        public int get(int pos) {
            checkRead(pos);
            return MemoryUtils.getInt(readAddress, pos);
        }
    }

    private class IntBufferW extends WriteBuffer implements IntWriteBuffer {
        @Override
        public void put(int value) {
            put(++writeCurrentPosition, value);
        }

        @Override
        public void put(int pos, int value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }
    }
}

