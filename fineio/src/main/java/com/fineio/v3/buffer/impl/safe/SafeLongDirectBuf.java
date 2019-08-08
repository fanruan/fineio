package com.fineio.v3.buffer.impl.safe;

import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.buffer.BufferAllocateFailedException;
import com.fineio.v3.buffer.BufferClosedException;
import com.fineio.v3.buffer.BufferOutOfBoundsException;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.buffer.LongDirectBuffer;

/**
 * @author anchore
 * @date 2019/7/29
 */
public class SafeLongDirectBuf extends BaseSafeDirectBuf<LongDirectBuffer> implements LongDirectBuffer {
    public SafeLongDirectBuf(LongDirectBuffer buf) {
        super(buf);
    }

    @Override
    public void putLong(int pos, long val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
        buf.putLong(pos, val);
    }

    @Override
    public long getLong(int pos) throws BufferClosedException, BufferOutOfBoundsException {
        return buf.getLong(pos);
    }

    @Override
    public void close() {
        DirectBuffer realBuf = buf;
        buf = new VoidLongDirectBuf(realBuf);
        try {
            // 等待最后一次读完就释放
            Thread.sleep(100);
        } catch (InterruptedException e) {
            FineIOLoggers.getLogger().error(e);
        } finally {
            realBuf.close();
        }
    }

    private static class VoidLongDirectBuf extends BaseVoidDirectBuf implements LongDirectBuffer {
        VoidLongDirectBuf(DirectBuffer realBuf) {
            super(realBuf);
        }

        @Override
        public void putLong(int pos, long val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
            throw new BufferClosedException(realBuf.getAddress(), getFileBlock());
        }

        @Override
        public long getLong(int pos) throws BufferClosedException, BufferOutOfBoundsException {
            throw new BufferClosedException(realBuf.getAddress(), getFileBlock());
        }
    }
}