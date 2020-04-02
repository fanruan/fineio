package com.fineio.io;

import com.fineio.accessor.buffer.DoubleBuf;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/19
 */
public class DoubleBuffer extends BaseBuffer<DoubleBuffer.DoubleReadBuffer, DoubleBuffer.DoubleWriteBuffer> implements DoubleBuf {
    public DoubleBuffer(Connector connector, URI uri, boolean syncWrite, Listener listener) {
        super(connector, uri, syncWrite, listener);
    }

    public DoubleBuffer(Connector connector, FileBlock block, int maxOffset, boolean syncWrite, Listener listener) {
        super(connector, block, maxOffset, syncWrite, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_DOUBLE;
    }

    @Override
    public DoubleWriteBuffer asWrite() {
        return new DoubleBufferW();
    }

    @Override
    public DoubleReadBuffer asRead() {
        return new DoubleBufferR();
    }

    public interface DoubleReadBuffer extends BufferR {
        double get(int pos);
    }

    public interface DoubleWriteBuffer extends BufferW {
        void put(double value);

        void put(int pos, double value);
    }

    private class DoubleBufferR extends ReadBuffer implements DoubleReadBuffer {
        @Override
        public double get(int pos) {
            checkRead(pos);
            return MemoryUtils.getDouble(readAddress, pos);
        }
    }

    private class DoubleBufferW extends WriteBuffer implements DoubleWriteBuffer {
        @Override
        public void put(double value) {
            put(++writeCurrentPosition, value);
        }

        @Override
        public void put(int pos, double value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }

    }
}
