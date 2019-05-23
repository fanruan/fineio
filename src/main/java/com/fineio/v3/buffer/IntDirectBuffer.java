package com.fineio.v3.buffer;

import com.fineio.accessor.buffer.IntBuf;

/**
 * @author anchore
 * @date 2019/4/11
 */
public interface IntDirectBuffer extends DirectBuffer, IntBuf {
    void putInt(int pos, int val)
            throws BufferClosedException, BufferAllocateFailedException,
            BufferOutOfBoundException;

    int getInt(int pos)
            throws BufferClosedException, BufferOutOfBoundException;
}