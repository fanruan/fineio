package com.fineio.v3.buffer;

import java.net.URI;

/**
 *
 */
public abstract class BaseBuffer implements Buffer {

    /**
     *
     */
    protected long address;
    /**
     *
     */
    protected URI uri;

    /**
     * Default constructor
     */
    public BaseBuffer() {
    }

    public long getAddress() {
        return address;
    }

    public URI getUri() {
        return uri;
    }
}