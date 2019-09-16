package com.fineio.io;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public interface ByteBuffer extends Buffer {
    byte getByte(int pos);

    void putByte(int pos, byte v);

    IntBuffer asInt();

    DoubleBuffer asDouble();

    LongBuffer asLong();
}
