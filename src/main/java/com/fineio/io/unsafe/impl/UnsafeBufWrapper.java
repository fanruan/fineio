package com.fineio.io.unsafe.impl;

import com.fineio.io.unsafe.UnsafeBuf;

import java.io.IOException;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class UnsafeBufWrapper implements UnsafeBuf {
    protected BaseUnsafeBuf unsafeBuf;

    public UnsafeBufWrapper(BaseUnsafeBuf unsafeBuf) {
        this.unsafeBuf = unsafeBuf;
    }

    @Override
    public void close() throws IOException {
        this.unsafeBuf.close();
    }
}
