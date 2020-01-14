package com.fineio.io.impl;

import com.fineio.io.DoubleBuffer;
import com.fineio.memory.MemoryUtils;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class DoubleBufferImpl extends BufferWrapper implements DoubleBuffer {

    DoubleBufferImpl(BaseBuffer unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public double getDouble(int pos) {
        return unsafeBuf.getDouble(pos);
    }

    @Override
    public void putDouble(int pos, double v) {
        unsafeBuf.putDouble(pos, v);
    }

}
