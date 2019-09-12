package com.fineio.v21.unsafe;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public interface DoubleUnsafeBuf extends UnsafeBuf {
    double getDouble(int pos);

    void putDouble(int pos, double v);
}
