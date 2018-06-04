package com.fineio.io.read;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface ShortReadBuffer extends ReadOnlyBuffer {
    short get(int pos);
}
