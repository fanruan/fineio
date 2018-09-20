package com.fineio.v2.io.read;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface LongReadBuffer extends ReadOnlyBuffer {
    long get(int pos);
}
