package com.fineio.io;

public interface FloatBuffer extends Buffer {
    int OFFSET = 2;

    void put(final int p0, final float p1);

    void put(final float p0);

    float get(final int p0);
}
