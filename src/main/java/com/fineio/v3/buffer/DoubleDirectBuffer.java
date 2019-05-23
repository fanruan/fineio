package com.fineio.v3.buffer;

import com.fineio.accessor.buffer.DoubleBuf;

/**
 * @author anchore
 * @date 2019/4/11
 */
public interface DoubleDirectBuffer extends DirectBuffer, DoubleBuf {
    void putDouble(int pos, double val)
            throws BufferClosedException, BufferAllocateFailedException,
            BufferOutOfBoundException;

    double getDouble(int pos)
            throws BufferClosedException, BufferOutOfBoundException;
}