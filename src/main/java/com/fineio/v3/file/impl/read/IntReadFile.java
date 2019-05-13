package com.fineio.v3.file.impl.read;

import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.buffer.impl.IntDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class IntReadFile extends ReadFile<IntDirectBuffer> {
    public IntReadFile(FileKey fileKey, Connector connector) {
        super(fileKey, Offset.INT, connector);
    }

    public int getInt(long pos) {
        checkPos(pos);
        return getBuffer(nthBuf(pos)).getInt(nthVal(pos));
    }

    @Override
    IntDirectBuffer newDirectBuf(long address, int size, FileKey fileKey) {
        return new IntDirectBuf(address, size, fileKey, size, FileMode.READ);
    }
}