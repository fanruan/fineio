package com.fineio.v3.buffer;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class BufferOutOfBoundException extends RuntimeException {
    public BufferOutOfBoundException(int pos, int cap) {
        super(String.format("pos %d, cap %d", pos, cap));
    }
}