package com.fineio.v2.io.write;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface LongWriteBuffer extends WriteOnlyBuffer {
    void put(int pos, long value);

    void put(long value);
}
