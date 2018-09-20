package com.fineio.v2.io.write;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface DoubleWriteBuffer extends WriteOnlyBuffer {
    void put(int pos, double value);

    void put(double value);
}
