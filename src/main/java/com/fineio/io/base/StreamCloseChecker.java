package com.fineio.io.base;

public abstract class StreamCloseChecker implements Checker {
    private int status;

    protected StreamCloseChecker(final int status) {
        this.status = status;
    }

    protected int getStatus() {
        return this.status;
    }
}
