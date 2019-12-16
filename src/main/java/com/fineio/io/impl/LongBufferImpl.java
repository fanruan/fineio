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
    public long getLong(int pos) {
        return unsafeBuf.getLong(pos);
    }

    @Override
    public void putLong(int pos, long v) {
        unsafeBuf.putLong(pos, v);
    }

}
