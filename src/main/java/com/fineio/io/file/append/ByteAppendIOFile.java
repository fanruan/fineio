package com.fineio.io.file.append;

import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.base.BufferKey;
import com.fineio.io.file.AppendIOFile;
import com.fineio.io.file.FileBlock;
import com.fineio.io.impl.BaseBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2019/9/12
 */
public class ByteAppendIOFile extends AppendIOFile<ByteBuffer> {
    public ByteAppendIOFile(Connector connector, URI uri) {
        super(connector, uri, (byte) MemoryConstants.OFFSET_BYTE);
    }

    public void put(byte value) {
        super.put(lastPos, value);
        lastPos++;
    }

    @Override
    public void put(int pos, byte v) {
        super.put(lastPos, v);
        lastPos++;
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
    protected Buffer createBuf(int idx) {
        return BaseBuffer.newAppendBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(idx))));
    }
}
