package com.fineio.v2_1.unsafe.impl;

import com.fineio.io.base.BufferKey;
import com.fineio.v2_1.unsafe.UnsafeBuf;

import java.io.IOException;
import java.io.InputStream;

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
    public void loadContent() {
        unsafeBuf.loadContent();
    }
}
