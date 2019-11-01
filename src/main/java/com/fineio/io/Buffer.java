package com.fineio.io;

import com.fineio.io.base.BufferKey;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public interface Buffer extends Closeable {
    InputStream asInputStream();

    BufferKey getBufferKey();

    long getMemorySize();

    Buffer flip();

    URI getUri();

    @Override
    void close();

    int getLength();
}
