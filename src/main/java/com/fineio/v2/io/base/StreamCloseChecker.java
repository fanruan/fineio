package com.fineio.v2.io.base;

import com.fineio.io.base.Checker;

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
