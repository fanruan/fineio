package com.fineio.v3.buffer.read;

import com.fineio.v3.buffer.BaseBuffer;

/**
 *
 */
public abstract class BaseReadBuffer extends BaseBuffer implements ReadBuffer {

    /**
     * Default constructor
     */
    BaseReadBuffer() {
    }

    /**
     *
     */
    @Override
    public void load() {
        // TODO implement here
    }

    @Override
    public void close() {

    }
}