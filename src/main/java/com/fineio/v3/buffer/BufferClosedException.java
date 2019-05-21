package com.fineio.v3.buffer;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class BufferClosedException extends RuntimeException {
    public BufferClosedException(long address) {
        super(String.format("address %x", address));
    }
}