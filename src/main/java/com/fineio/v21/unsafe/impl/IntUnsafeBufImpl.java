package com.fineio.v21.unsafe.impl;

import com.fineio.memory.MemoryUtils;
import com.fineio.v21.unsafe.IntUnsafeBuf;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class IntUnsafeBufImpl extends UnsafeBufWrapper implements IntUnsafeBuf {

    IntUnsafeBufImpl(BaseUnsafeBuf unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public int getInt(int pos) {
        final int offset = unsafeBuf.ensurePos(pos);
        return MemoryUtils.getInt(unsafeBuf.getAddress(), offset);
    }

    @Override
    public void putInt(int pos, int v) {
        final int offset = unsafeBuf.ensureCap(pos);
        MemoryUtils.put(unsafeBuf.getAddress(), offset, v);
    }

}
