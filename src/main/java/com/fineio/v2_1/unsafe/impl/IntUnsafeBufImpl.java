package com.fineio.v2_1.unsafe.impl;

import com.fineio.memory.MemoryUtils;
import com.fineio.v2_1.unsafe.IntUnsafeBuf;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class IntUnsafeBufImpl extends UnsafeBufWrapper implements IntUnsafeBuf {
    private BaseUnsafeBuf unsafeBuf;

    IntUnsafeBufImpl(BaseUnsafeBuf unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public int getInt(int pos) {
        return MemoryUtils.getInt(unsafeBuf.getAddress(), unsafeBuf.ensurePos(pos));
    }

    @Override
    public void putInt(int pos, int v) {
        MemoryUtils.put(unsafeBuf.getAddress(), unsafeBuf.ensureCap(pos), v);
    }

}
