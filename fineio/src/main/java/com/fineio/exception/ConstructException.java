package com.fineio.exception;

/**
 * Created by daniel on 2017/2/10.
 */
public class ConstructException extends RuntimeException {

    public ConstructException(Exception e) {
        super(e);
    }
}
