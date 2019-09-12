package com.fineio.v21.unsafe.impl;

import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.v21.unsafe.ByteUnsafeBuf;
import com.fineio.v21.unsafe.DoubleUnsafeBuf;
import com.fineio.v21.unsafe.IntUnsafeBuf;
import com.fineio.v21.unsafe.LongUnsafeBuf;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2019/9/10
 */
public class ByteUnsafeBufImpl extends UnsafeBufWrapper implements ByteUnsafeBuf {

    ByteUnsafeBufImpl(BaseUnsafeBuf unsafeBuf) {
        super(unsafeBuf);
    }

    @Override
    public byte getByte(int pos) {
        final int offset = unsafeBuf.ensurePos(pos);
        return MemoryUtils.getByte(unsafeBuf.getAddress(), offset);
    }

    @Override
    public void putByte(int pos, byte v) {
        final int offset = unsafeBuf.ensureCap(pos);
        MemoryUtils.put(unsafeBuf.getAddress(), offset, v);
    }

    @Override
    public IntUnsafeBuf asInt() {
        return new IntUnsafeBufImpl(unsafeBuf.setOffset(MemoryConstants.OFFSET_INT));
    }

    @Override
    public DoubleUnsafeBuf asDouble() {
        return new DoubleUnsafeBufImpl(unsafeBuf.setOffset(MemoryConstants.OFFSET_DOUBLE));
    }

    @Override
    public LongUnsafeBuf asLong() {
        return new LongUnsafeBufImpl(unsafeBuf.setOffset(MemoryConstants.OFFSET_LONG));
    }

}
