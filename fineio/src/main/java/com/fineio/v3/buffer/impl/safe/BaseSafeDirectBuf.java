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

    @Override
    public int getCapInBytes() {
        return buf.getCapInBytes();
    }

    @Override
    public void letGcHelpRelease() {
        buf.letGcHelpRelease();
    }

    static class BaseVoidDirectBuf implements DirectBuffer {
        long address;
        FileBlock fileBlock;

        public BaseVoidDirectBuf(long address, FileBlock fileBlock) {
            this.address = address;
            this.fileBlock = fileBlock;
        }

        @Override
        public FileBlock getFileBlock() {
            throw new BufferClosedException(address, fileBlock);
        }

        @Override
        public long getAddress() {
            throw new BufferClosedException(address, fileBlock);
        }

        @Override
        public int getSizeInBytes() {
            throw new BufferClosedException(address, fileBlock);
        }

        @Override
        public int getCapInBytes() {
            throw new BufferClosedException(address, fileBlock);
        }

        @Override
        public void close() {
        }

        @Override
        public void letGcHelpRelease() {
        }
    }
}