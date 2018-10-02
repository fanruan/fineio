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
public class FloatBuffer extends BaseBuffer<FloatBuffer.FloatReadBuffer, FloatBuffer.FloatWriteBuffer> {
    public FloatBuffer(Connector connector, FileBlock block, int maxOffset, Listener listener) {
        super(connector, block, maxOffset, listener);
    }

    public FloatBuffer(Connector connector, URI uri, Listener listener) {
        super(connector, uri, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_FLOAT;
    }

    @Override
    public FloatWriteBuffer asWrite() {
        return new FloatBufferW();
    }

    @Override
    public FloatReadBuffer asRead() {
        return new FloatBufferR();
    }

    public interface FloatReadBuffer extends BufferR {
        float get(int pos);
    }

    public interface FloatWriteBuffer extends BufferW {
        void put(float value);

        void put(int pos, float value);
    }

    private class FloatBufferR extends ReadBuffer implements FloatReadBuffer {
        @Override
        public float get(int pos) {
            checkRead(pos);
            return MemoryUtils.getFloat(readAddress, pos);
        }
    }

    private class FloatBufferW extends WriteBuffer implements FloatWriteBuffer {
        @Override
        public void put(float value) {
            put(++writeCurrentPosition, value);
        }

        @Override
        public void put(int pos, float value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }
    }
}

