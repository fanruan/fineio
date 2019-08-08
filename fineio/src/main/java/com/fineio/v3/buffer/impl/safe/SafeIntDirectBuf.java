package com.fineio.v3.buffer.impl.safe;

import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.buffer.BufferAllocateFailedException;
import com.fineio.v3.buffer.BufferClosedException;
import com.fineio.v3.buffer.BufferOutOfBoundsException;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.buffer.IntDirectBuffer;

/**
 * @author anchore
 * @date 2019/7/29
 */
public class SafeIntDirectBuf extends BaseSafeDirectBuf<IntDirectBuffer> implements IntDirectBuffer {
    public SafeIntDirectBuf(IntDirectBuffer buf) {
        super(buf);
    }

    @Override
    public void putInt(int pos, int val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
        buf.putInt(pos, val);
    }

    @Override
    public int getInt(int pos) throws BufferClosedException, BufferOutOfBoundsException {
        return buf.getInt(pos);
    }

    @Override
    public void close() {
        DirectBuffer realBuf = buf;
        buf = new VoidIntDirectBuf(realBuf);
        try {
            // 等待最后一次读完就释放
            Thread.sleep(100);
        } catch (InterruptedException e) {
            FineIOLoggers.getLogger().error(e);
        } finally {
            realBuf.close();
        }
    }

    private static class VoidIntDirectBuf extends BaseVoidDirectBuf implements IntDirectBuffer {
        VoidIntDirectBuf(DirectBuffer realBuf) {
            super(realBuf);
        }

        @Override
        public void putInt(int pos, int val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
            throw new BufferClosedException(realBuf.getAddress(), getFileBlock());
        }

        @Override
        public int getInt(int pos) throws BufferClosedException, BufferOutOfBoundsException {
            throw new BufferClosedException(realBuf.getAddress(), getFileBlock());
        }
    }
}