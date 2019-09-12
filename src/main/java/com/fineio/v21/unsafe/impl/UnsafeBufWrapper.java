package com.fineio.v21.unsafe.impl;

import com.fineio.io.base.BufferKey;
import com.fineio.v21.unsafe.UnsafeBuf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class UnsafeBufWrapper implements UnsafeBuf {
    BaseUnsafeBuf unsafeBuf;

    UnsafeBufWrapper(BaseUnsafeBuf unsafeBuf) {
        this.unsafeBuf = unsafeBuf;
    }

    @Override
    public void close() throws IOException {
        this.unsafeBuf.close();
    }

    @Override
    public InputStream asInputStream() {
        return this.unsafeBuf.asInputStream();
    }

    @Override
    public BufferKey getBufferKey() {
        return this.unsafeBuf.getBufferKey();
    }

    @Override
    public long getMemorySize() {
        return this.unsafeBuf.getMemorySize();
    }

    @Override
    public UnsafeBuf flip() {
        unsafeBuf.flip();
        return this;
    }

    @Override
    public URI getUri() {
        return this.unsafeBuf.getUri();
    }

}
