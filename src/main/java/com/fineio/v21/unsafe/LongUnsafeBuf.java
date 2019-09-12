package com.fineio.v21.unsafe;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public interface LongUnsafeBuf extends UnsafeBuf {
    long getLong(int pos);

    void putLong(int pos, long v);
}
