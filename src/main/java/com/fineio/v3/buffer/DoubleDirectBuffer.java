package com.fineio.v3.buffer;

/**
 * @author anchore
 * @date 2019/4/11
 */
public interface DoubleDirectBuffer extends DirectBuffer {
    void putDouble(int pos, double val);

    double getDouble(int pos);
}