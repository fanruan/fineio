package com.fineio.io;

public interface LongBuffer extends Buffer {
    int OFFSET = 3;

    void put(final int p0, final long p1);

    void put(final long p0);

    long get(final int p0);
}
