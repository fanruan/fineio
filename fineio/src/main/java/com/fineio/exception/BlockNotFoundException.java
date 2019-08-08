package com.fineio.exception;

/**
 * Created by daniel on 2017/2/10.
 */
public class BlockNotFoundException extends RuntimeException {

    public BlockNotFoundException(String s) {
        super(s);
    }

    public BlockNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
