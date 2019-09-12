package com.fineio.io;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public interface DoubleBuffer extends Buffer {
    double getDouble(int pos);

    void putDouble(int pos, double v);
}
