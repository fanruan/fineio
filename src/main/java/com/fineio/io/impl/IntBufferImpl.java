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
    public int getInt(int pos) {
        return unsafeBuf.getInt(pos);
    }

    @Override
    public void putInt(int pos, int v) {
        unsafeBuf.putInt(pos, v);
    }

}
