package com.fineio.v2.io.write;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface ShortWriteBuffer extends WriteOnlyBuffer {
    void put(int pos, short value);

    void put(short value);
}
