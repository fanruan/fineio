package com.fineio.io.unsafe.impl;

import com.fineio.io.unsafe.IntUnsafeBuf;
import com.fineio.memory.MemoryUtils;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class IntUnsafeBufImpl extends UnsafeBufWrapper implements IntUnsafeBuf {
    private BaseUnsafeBuf unsafeBuf;

    public IntUnsafeBufImpl(BaseUnsafeBuf unsafeBuf) {
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
