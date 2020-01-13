package com.fineio.io.impl;

import com.fineio.io.ByteBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.memory.MemoryConstants;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class ByteBufferImpl extends BufferWrapper implements ByteBuffer {

    ByteBufferImpl(BaseBuffer unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public byte getByte(int pos) {
        return unsafeBuf.getByte(pos);
    }

    @Override
    public void putByte(int pos, byte v) {
        unsafeBuf.putByte(pos, v);
    }

    @Override
    public ByteBuffer asByte() {
        return new ByteBufferImpl(unsafeBuf.setOffset(MemoryConstants.OFFSET_BYTE));
    }

    @Override
    public IntBuffer asInt() {
        return new IntBufferImpl(unsafeBuf.setOffset(MemoryConstants.OFFSET_INT));
    }

    @Override
    public DoubleBuffer asDouble() {
        return new DoubleBufferImpl(unsafeBuf.setOffset(MemoryConstants.OFFSET_DOUBLE));
    }

    @Override
    public LongBuffer asLong() {
        return new LongBufferImpl(unsafeBuf.setOffset(MemoryConstants.OFFSET_LONG));
    }

}
