package com.fineio.v21.unsafe;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public interface ByteUnsafeBuf extends UnsafeBuf {
    byte getByte(int pos);

    void putByte(int pos, byte v);

    IntUnsafeBuf asInt();

    DoubleUnsafeBuf asDouble();

    LongUnsafeBuf asLong();
}
