package com.fineio.v2.io.write;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface IntWriteBuffer extends WriteOnlyBuffer {
    void put(int pos, int value);

    void put(int value);
}
