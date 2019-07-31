package com.fineio.v3.buffer.impl.safe;

import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.BufferClosedException;
import com.fineio.v3.buffer.DirectBuffer;

/**
 * @author anchore
 * @date 2019/7/29
 */
abstract class BaseSafeDirectBuf<B extends DirectBuffer> implements DirectBuffer {
    volatile B buf;

    BaseSafeDirectBuf(B buf) {
        this.buf = buf;
    }

    @Override
    public FileBlock getFileBlock() {
        return buf.getFileBlock();
    }

    @Override
    public long getAddress() {
        return buf.getAddress();
    }

    @Override
    public int getSizeInBytes() {
        return buf.getSizeInBytes();
    }

    static class BaseVoidDirectBuf implements DirectBuffer {
        DirectBuffer realBuf;

        BaseVoidDirectBuf(DirectBuffer realBuf) {
            this.realBuf = realBuf;
        }

        @Override
        public FileBlock getFileBlock() {
            return realBuf.getFileBlock();
        }

        @Override
        public long getAddress() {
            throw new BufferClosedException(realBuf.getAddress(), getFileBlock());
        }

        @Override
        public int getSizeInBytes() {
            return realBuf.getSizeInBytes();
        }

        @Override
        public void close() {
        }
    }
}