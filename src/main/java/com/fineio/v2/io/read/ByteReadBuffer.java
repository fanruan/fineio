package com.fineio.v2.io.read;

/**
 * @author yee
 * @date 2018/6/1
 */
public interface ByteReadBuffer extends ReadOnlyBuffer {
    byte get(int pos);
}
