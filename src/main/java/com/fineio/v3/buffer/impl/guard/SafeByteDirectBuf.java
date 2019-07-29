package com.fineio.v3.buffer.impl.guard;

import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.buffer.BufferAllocateFailedException;
import com.fineio.v3.buffer.BufferClosedException;
import com.fineio.v3.buffer.BufferOutOfBoundsException;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.DirectBuffer;

/**
 * @author anchore
 * @date 2019/7/29
 */
public class SafeByteDirectBuf extends BaseSafeDirectBuf<ByteDirectBuffer> implements ByteDirectBuffer {
    public SafeByteDirectBuf(ByteDirectBuffer buf) {
        super(buf);
    }

    @Override
    public void putByte(int pos, byte val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
        buf.putByte(pos, val);
    }

    @Override
    public byte getByte(int pos) throws BufferClosedException, BufferOutOfBoundsException {
        return buf.getByte(pos);
    }

    @Override
    public void close() {
        DirectBuffer realBuf = buf;
        buf = new VoidByteDirectBuf(realBuf);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            FineIOLoggers.getLogger().error(e);
        } finally {
            realBuf.close();
        }
    }

    private static class VoidByteDirectBuf extends BaseVoidDirectBuf implements ByteDirectBuffer {
        VoidByteDirectBuf(DirectBuffer realBuf) {
            super(realBuf);
        }

        @Override
        public void putByte(int pos, byte val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
            throw new BufferClosedException(getAddress(), getFileBlock());
        }

        @Override
        public byte getByte(int pos) throws BufferClosedException, BufferOutOfBoundsException {
            throw new BufferClosedException(getAddress(), getFileBlock());
        }
    }
}