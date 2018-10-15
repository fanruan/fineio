package com.fineio.exception;

/**
 * Created by daniel on 2017/2/13.
 */
public class BufferIndexOutOfBoundsException extends ArrayIndexOutOfBoundsException {

    public BufferIndexOutOfBoundsException(long index) {
        super((int) index);
    }
}
