package com.fineio.v3.file.impl.read;

import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class LongReadFile extends ReadFile<LongDirectBuffer> {
    public LongReadFile(FileKey fileKey, Connector connector) {
        super(fileKey, Offset.LONG, connector);
    }

    public long getLong(long pos) {
        checkPos(pos);
        return getBuffer(nthBuf(pos)).getLong(nthVal(pos));
    }

    @Override
    LongDirectBuffer newDirectBuf(long address, int size, FileKey fileKey) {
        return new LongDirectBuf(address, size, fileKey, size, FileMode.READ);
    }
}