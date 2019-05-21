package com.fineio.v3.buffer;

/**
 * @author anchore
 * @date 2019/4/11
 */
public interface IntDirectBuffer extends DirectBuffer {
    void putInt(int pos, int val)
            throws BufferClosedException, BufferAllocateFailedException,
            BufferOutOfBoundException;

    int getInt(int pos)
            throws BufferClosedException, BufferOutOfBoundException;
}