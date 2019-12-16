package com.fineio.io.impl;

import com.fineio.io.Buffer;
import com.fineio.io.Level;
import com.fineio.io.base.BufferKey;

import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class BufferWrapper implements Buffer {
    BaseBuffer unsafeBuf;

    BufferWrapper(BaseBuffer unsafeBuf) {
        this.unsafeBuf = unsafeBuf;
    }

    @Override
    public void close() {
        this.unsafeBuf.close();
        unsafeBuf = null;
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
    public Buffer flip() {
        unsafeBuf.flip();
        return this;
    }

    @Override
    public URI getUri() {
        return this.unsafeBuf.getUri();
    }

    @Override
    public int getLength() {
        return this.unsafeBuf.getLength();
    }

    @Override
    public Level getLevel() {
        return unsafeBuf.getLevel();
    }

}
