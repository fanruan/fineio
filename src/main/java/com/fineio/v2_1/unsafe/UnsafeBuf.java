package com.fineio.v2_1.unsafe;

import com.fineio.io.base.BufferKey;

import java.io.Closeable;
import java.io.InputStream;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public interface UnsafeBuf extends Closeable {
    InputStream asInputStream();

    BufferKey getBufferKey();
}
