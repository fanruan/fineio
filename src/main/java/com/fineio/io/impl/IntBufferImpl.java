package com.fineio.io.impl;

import com.fineio.io.IntBuffer;
import com.fineio.memory.MemoryUtils;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class IntBufferImpl extends BufferWrapper implements IntBuffer {

    IntBufferImpl(BaseBuffer unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public synchronized int getInt(int pos) {
        final int offset = unsafeBuf.ensurePos(pos);
        return MemoryUtils.getInt(unsafeBuf.getAddress(), offset);
    }

    @Override
    public void putInt(int pos, int v) {
        final int offset = unsafeBuf.ensureCap(pos);
        MemoryUtils.put(unsafeBuf.getAddress(), offset, v);
    }

}
