package com.fineio.io.impl;

import com.fineio.io.LongBuffer;
import com.fineio.memory.MemoryUtils;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class LongBufferImpl extends BufferWrapper implements LongBuffer {

    LongBufferImpl(BaseBuffer unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public synchronized long getLong(int pos) {
        final int offset = unsafeBuf.ensurePos(pos);
        return MemoryUtils.getLong(unsafeBuf.getAddress(), offset);
    }

    @Override
    public void putLong(int pos, long v) {
        final int offset = unsafeBuf.ensureCap(pos);
        MemoryUtils.put(unsafeBuf.getAddress(), offset, v);
    }

}
