package com.fineio.io;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public interface IntBuffer extends Buffer {
    int getInt(int pos);

    void putInt(int pos, int v);
}
