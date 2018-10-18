package com.fineio.io;

public interface CharBuffer extends Buffer {
    int OFFSET = 1;

    void put(final int p0, final char p1);

    void put(final char p0);

    char get(final int p0);
}
