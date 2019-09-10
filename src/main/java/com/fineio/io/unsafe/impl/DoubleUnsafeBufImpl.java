package com.fineio.io.unsafe.impl;

import com.fineio.io.unsafe.DoubleUnsafeBuf;
import com.fineio.memory.MemoryUtils;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class DoubleUnsafeBufImpl extends UnsafeBufWrapper implements DoubleUnsafeBuf {

    public DoubleUnsafeBufImpl(BaseUnsafeBuf unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public double getDouble(int pos) {
        return MemoryUtils.getDouble(unsafeBuf.getAddress(), unsafeBuf.ensurePos(pos));
    }

    @Override
    public void putDouble(int pos, double v) {
        MemoryUtils.put(unsafeBuf.getAddress(), unsafeBuf.ensureCap(pos), v);
    }

}
