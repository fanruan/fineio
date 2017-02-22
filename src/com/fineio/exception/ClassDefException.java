package com.fineio.exception;

/**
 * Created by daniel on 2017/2/22.
 */
public class ClassDefException extends RuntimeException {

    public ClassDefException(Exception e){
        super(e);
    }
}
