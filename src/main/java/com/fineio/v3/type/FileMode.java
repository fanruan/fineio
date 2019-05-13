package com.fineio.v3.type;

import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public enum FileMode {
    //
    READ,
    WRITE,
    APPEND;
    private ReentrantLock lock = new ReentrantLock();

    public ReentrantLock getLock() {
        return lock;
    }
}