package com.fineio.v2_1.unsafe.impl;

import com.fineio.memory.MemoryUtils;
import com.fineio.v2_1.unsafe.DoubleUnsafeBuf;

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
        return MemoryUtils.getDouble(unsafeBuf.getAddress(), unsafeBuf.ensurePos(pos));
    }

    @Override
    public void putDouble(int pos, double v) {
        MemoryUtils.put(unsafeBuf.getAddress(), unsafeBuf.ensureCap(pos), v);
    }

}
