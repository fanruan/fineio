package com.fineio.v2.io.write;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface ByteWriteBuffer extends WriteOnlyBuffer {
    void put(int pos, byte value);

    void put(byte value);
}
