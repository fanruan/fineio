package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class DoubleWriteFile extends WriteFile<DoubleDirectBuffer> {
    public DoubleWriteFile(FileKey fileKey, Connector connector) {
        super(fileKey, Offset.DOUBLE, connector);
    }

    public void putDouble(long pos, double value) {
        checkPos(pos);
        getBuffer(nthBuf(pos)).putDouble(nthVal(pos), value);
    }

    @Override
    protected DoubleDirectBuffer getBuffer(int nthBuf) {
        return buffers.computeIfAbsent(nthBuf,
                i -> new DoubleDirectBuf(new FileKey(fileKey.getPath(), String.valueOf(i))));
    }
}