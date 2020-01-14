package com.fineio.exception;

public class BufferClosedException extends RuntimeException {

    public BufferClosedException(String s) {
        super(s);
    }

    public BufferClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
