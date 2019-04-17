package com.fineio.v3.buffer;

/**
 * @author anchore
 * @date 2019/4/11
 */
public interface IntDirectBuffer extends DirectBuffer {
    void putInt(int pos, int val);

    int getInt(int pos);
}