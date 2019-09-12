package com.fineio.v21.unsafe.impl;

import com.fineio.memory.MemoryUtils;
import com.fineio.v21.unsafe.DoubleUnsafeBuf;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class DoubleUnsafeBufImpl extends UnsafeBufWrapper implements DoubleUnsafeBuf {

    DoubleUnsafeBufImpl(BaseUnsafeBuf unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public double getDouble(int pos) {
        final int offset = unsafeBuf.ensurePos(pos);
        return MemoryUtils.getDouble(unsafeBuf.getAddress(), offset);
    }

    @Override
    public void putDouble(int pos, double v) {
        final int offset = unsafeBuf.ensureCap(pos);
        MemoryUtils.put(unsafeBuf.getAddress(), offset, v);
    }

}
