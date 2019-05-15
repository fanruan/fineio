package com.fineio.v3.exception;

/**
 * @author yee
 * @date 2019-05-15
 */
public class OutOfDirectMemoryException extends Exception {
    public OutOfDirectMemoryException(Throwable cause) {
        super(cause);
    }

    public OutOfDirectMemoryException(String s) {
    }
}
