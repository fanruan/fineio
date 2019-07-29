package com.fineio.v3.buffer.impl.guard;

import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.buffer.BufferAllocateFailedException;
import com.fineio.v3.buffer.BufferClosedException;
import com.fineio.v3.buffer.BufferOutOfBoundsException;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.buffer.DoubleDirectBuffer;

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
        DirectBuffer realBuf = buf;
        buf = new VoidDoubleDirectBuf(realBuf);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            FineIOLoggers.getLogger().error(e);
        } finally {
            realBuf.close();
        }
    }

    static class VoidDoubleDirectBuf extends BaseVoidDirectBuf implements DoubleDirectBuffer {
        VoidDoubleDirectBuf(DirectBuffer realBuf) {
            super(realBuf);
        }

        @Override
        public void putDouble(int pos, double val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
            throw new BufferClosedException(getAddress(), getFileBlock());
        }

        @Override
        public double getDouble(int pos) throws BufferClosedException, BufferOutOfBoundsException {
            throw new BufferClosedException(getAddress(), getFileBlock());
        }
    }
}