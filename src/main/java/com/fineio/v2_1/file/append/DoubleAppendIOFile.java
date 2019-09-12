package com.fineio.v2_1.file.append;

import com.fineio.io.base.BufferKey;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;
import com.fineio.v2_1.unsafe.ByteUnsafeBuf;
import com.fineio.v2_1.unsafe.UnsafeBuf;
import com.fineio.v2_1.unsafe.impl.BaseUnsafeBuf;

import java.net.URI;

/**
 * @author yee
 * @date 2019/9/12
 */
public class DoubleAppendIOFile extends AppendIOFile<ByteUnsafeBuf> {
    protected DoubleAppendIOFile(Connector connector, URI uri) {
        super(connector, uri, (byte) MemoryConstants.OFFSET_DOUBLE);
    }

    public void put(double value) {
        super.put(lastPos++, value);
    }

    @Override
    public void put(int pos, byte v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(int pos, int v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(int pos, long v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(int pos, double v) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected UnsafeBuf createBuf(int idx) {
        return BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(idx)))).asDouble();
    }
}
