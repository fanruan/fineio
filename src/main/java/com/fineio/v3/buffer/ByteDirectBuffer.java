package com.fineio.v3.buffer;

/**
 * @author anchore
 * @date 2019/4/11
 */
public interface ByteDirectBuffer extends DirectBuffer {
    void putByte(int pos, byte val)
            throws BufferClosedException, BufferAllocateFailedException,
            BufferOutOfBoundException;

    byte getByte(int pos)
            throws BufferClosedException, BufferOutOfBoundException;
}