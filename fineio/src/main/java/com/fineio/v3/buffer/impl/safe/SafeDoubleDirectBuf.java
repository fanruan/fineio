package com.fineio.v3.buffer.impl.safe;

import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.buffer.*;

/**
 * @author anchore
 * @date 2019/7/29
 */
public class SafeDoubleDirectBuf extends BaseSafeDirectBuf<DoubleDirectBuffer> implements DoubleDirectBuffer {
    public SafeDoubleDirectBuf(DoubleDirectBuffer buf) {
        super(buf);
    }

    @Override
    public void putDouble(int pos, double val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
        buf.putDouble(pos, val);
    }

    @Override
    public double getDouble(int pos) throws BufferClosedException, BufferOutOfBoundsException {
        return buf.getDouble(pos);
    }

    @Override
    public void close() {
        long address = buf.getAddress();
        FileBlock fileBlock = buf.getFileBlock();
        buf = new VoidDoubleDirectBuf(address, fileBlock);
    }

    private static class VoidDoubleDirectBuf extends BaseVoidDirectBuf implements DoubleDirectBuffer {
        public VoidDoubleDirectBuf(long address, FileBlock fileBlock) {
            super(address, fileBlock);
        }

        @Override
        public void putDouble(int pos, double val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
            throw new BufferClosedException(address, fileBlock);
        }

        @Override
        public double getDouble(int pos) throws BufferClosedException, BufferOutOfBoundsException {
            throw new BufferClosedException(address, fileBlock);
        }
    }
}