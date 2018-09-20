package com.fineio.v2.io.read;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface FloatReadBuffer extends ReadOnlyBuffer {
    float get(int pos);
}
