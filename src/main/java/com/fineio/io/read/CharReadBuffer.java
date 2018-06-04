package com.fineio.io.read;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface CharReadBuffer extends ReadOnlyBuffer {
    char get(int pos);
}
