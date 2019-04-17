package com.fineio.v3.file.impl.read;

import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class DoubleReadFile extends ReadFile<DoubleDirectBuffer> {
    public DoubleReadFile(FileKey fileKey, Connector connector) {
        super(fileKey, Offset.DOUBLE, connector);
    }

    public double getDouble(long pos) {
        checkPos(pos);
        return getBuffer(nthBuf(pos)).getDouble(nthVal(pos));
    }

    @Override
    DoubleDirectBuffer newDirectBuf(long address, int size, FileKey fileKey) {
        return new DoubleDirectBuf(address, size, fileKey);
    }
}