package com.fineio.io;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * @author yee
 * @date 2018/9/19
 */
public class DoubleBuffer extends BaseBuffer<DoubleBuffer.DoubleReadBuffer, DoubleBuffer.DoubleWriteBuffer> {
    public DoubleBuffer(Connector connector, FileBlock block, int maxOffset, Listener listener) {
        super(connector, block, maxOffset, listener);
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

