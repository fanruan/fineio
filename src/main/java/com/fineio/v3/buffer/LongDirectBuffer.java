package com.fineio.v3.buffer;

/**
 * @author anchore
 * @date 2019/4/11
 */
public interface LongDirectBuffer extends DirectBuffer {
    void putLong(int pos, long val);

    long getLong(int pos);
}