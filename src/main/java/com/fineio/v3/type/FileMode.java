package com.fineio.v3.type;

import java.util.concurrent.locks.Condition;
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
    private Condition condition = lock.newCondition();

    public ReentrantLock getLock() {
        return lock;
    }

    public Condition getCondition() {
        return condition;
    }
}