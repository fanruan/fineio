package com.fineio.v21.unsafe.impl;

import com.fineio.memory.MemoryUtils;
import com.fineio.v21.unsafe.LongUnsafeBuf;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class LongUnsafeBufImpl extends UnsafeBufWrapper implements LongUnsafeBuf {

    LongUnsafeBufImpl(BaseUnsafeBuf unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public long getLong(int pos) {
        final int offset = unsafeBuf.ensurePos(pos);
        return MemoryUtils.getLong(unsafeBuf.getAddress(), offset);
    }

    @Override
    public void putLong(int pos, long v) {
        final int offset = unsafeBuf.ensureCap(pos);
        MemoryUtils.put(unsafeBuf.getAddress(), offset, v);
    }

}
