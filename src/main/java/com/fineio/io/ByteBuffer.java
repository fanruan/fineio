package com.fineio.io;

public interface ByteBuffer extends Buffer {
    int OFFSET = 0;

    void put(final int p0, final byte p1);

    void put(final byte p0);

    byte get(final int p0);
}
