package com.fineio.v3.buffer;

import com.fineio.accessor.buffer.ByteBuf;

/**
 * @author anchore
 * @date 2019/4/11
 */
public interface ByteDirectBuffer extends DirectBuffer, ByteBuf {
    void putByte(int pos, byte val)
            throws BufferClosedException, BufferAllocateFailedException,
            BufferOutOfBoundsException;

    byte getByte(int pos)
            throws BufferClosedException, BufferOutOfBoundsException;
}