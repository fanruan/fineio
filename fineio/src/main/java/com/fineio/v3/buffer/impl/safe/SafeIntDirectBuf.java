package com.fineio.v3.buffer.impl.safe;

import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.buffer.*;

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
        long address = buf.getAddress();
        FileBlock fileBlock = buf.getFileBlock();
        buf = new VoidIntDirectBuf(address, fileBlock);
    }

    private static class VoidIntDirectBuf extends BaseVoidDirectBuf implements IntDirectBuffer {
        public VoidIntDirectBuf(long address, FileBlock fileBlock) {
            super(address, fileBlock);
        }

        @Override
        public void putInt(int pos, int val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
            throw new BufferClosedException(address, fileBlock);
        }

        @Override
        public int getInt(int pos) throws BufferClosedException, BufferOutOfBoundsException {
            throw new BufferClosedException(address, fileBlock);
        }
    }
}