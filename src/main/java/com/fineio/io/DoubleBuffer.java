package com.fineio.io;

public interface DoubleBuffer extends Buffer {
    int OFFSET = 3;

    void put(final int p0, final double p1);

    void put(final double p0);

    double get(final int p0);
}
