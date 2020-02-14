package com.fineio.v3.buffer.impl.safe;

import com.fineio.io.file.FileBlock;
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
        long address = buf.getAddress();
        FileBlock fileBlock = buf.getFileBlock();
        buf = new VoidByteDirectBuf(address, fileBlock);
    }

    private static class VoidByteDirectBuf extends BaseVoidDirectBuf implements ByteDirectBuffer {
        public VoidByteDirectBuf(long address, FileBlock fileBlock) {
            super(address, fileBlock);
        }

        @Override
        public void putByte(int pos, byte val) throws BufferClosedException, BufferAllocateFailedException, BufferOutOfBoundsException {
            throw new BufferClosedException(address, fileBlock);
        }

        @Override
        public byte getByte(int pos) throws BufferClosedException, BufferOutOfBoundsException {
            throw new BufferClosedException(address, fileBlock);
        }
    }
}