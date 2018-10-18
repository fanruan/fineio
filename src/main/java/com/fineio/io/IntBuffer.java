package com.fineio.io;

public interface IntBuffer extends Buffer {
    int OFFSET = 2;

    void put(final int p0, final int p1);

    void put(final int p0);

    int get(final int p0);
}
