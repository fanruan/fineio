package com.fineio.io;

public interface ShortBuffer extends Buffer {
    int OFFSET = 1;

    void put(final int p0, final short p1);

    void put(final short p0);

    short get(final int p0);
}
