package com.fineio.io.unsafe.impl;

import com.fineio.io.unsafe.LongUnsafeBuf;
import com.fineio.memory.MemoryUtils;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class LongUnsafeBufImpl extends UnsafeBufWrapper implements LongUnsafeBuf {

    public LongUnsafeBufImpl(BaseUnsafeBuf unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public long getLong(int pos) {
        return MemoryUtils.getLong(unsafeBuf.getAddress(), unsafeBuf.ensurePos(pos));
    }

    @Override
    public void putLong(int pos, long v) {
        MemoryUtils.put(unsafeBuf.getAddress(), unsafeBuf.ensureCap(pos), v);
    }

}
