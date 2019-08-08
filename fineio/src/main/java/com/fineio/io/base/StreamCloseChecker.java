package com.fineio.io.base;

/**
 * Created by daniel on 2017/2/24.
 */
public abstract class StreamCloseChecker implements Checker {

    private int status;

    protected StreamCloseChecker(int status) {
        this.status = status;
    }

    protected int getStatus() {
        return status;
    }

}
