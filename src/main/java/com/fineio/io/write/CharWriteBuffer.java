package com.fineio.io.write;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface CharWriteBuffer extends WriteOnlyBuffer {
    void put(int pos, char value);

    void put(char value);
}
