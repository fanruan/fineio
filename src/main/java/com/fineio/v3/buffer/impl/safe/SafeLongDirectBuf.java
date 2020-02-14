package com.fineio.v3.buffer.impl.safe;

import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.BufferAllocateFailedException;
import com.fineio.v3.buffer.BufferClosedException;
import com.fineio.v3.buffer.BufferOutOfBoundsException;
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
        long address = buf.getAddress();
        FileBlock fileBlock = buf.getFileBlock();
        buf = new VoidLongDirectBuf(address, fileBlock);
    }

    private static class VoidLongDirectBuf extends BaseVoidDirectBuf implements LongDirectBuffer {
        public VoidLongDirectBuf(long address, FileBlock fileBlock) {
            super(address, fileBlock);
        }

        @Override
        public void putLong(int pos, long val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
            throw new BufferClosedException(address, fileBlock);
        }

        @Override
        public long getLong(int pos) throws BufferClosedException, BufferOutOfBoundsException {
            throw new BufferClosedException(address, fileBlock);
        }
    }
}