package com.fineio.v3.file.impl.read;

import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class ByteReadFile extends ReadFile<ByteDirectBuffer> {
    public ByteReadFile(FileKey fileKey, Connector connector) {
        super(fileKey, Offset.BYTE, connector);
    }

    public byte getByte(long pos) {
        checkPos(pos);
        return getBuffer(nthBuf(pos)).getByte(nthVal(pos));
    }

    @Override
    ByteDirectBuffer newDirectBuf(long address, int size, FileKey fileKey) {
        return new ByteDirectBuf(address, size, fileKey, size, FileMode.READ);
    }
}