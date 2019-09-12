package com.fineio.v21.unsafe;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public interface IntUnsafeBuf extends UnsafeBuf {
    int getInt(int pos);

    void putInt(int pos, int v);
}
