package com.fineio.v3.file.impl;

import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.file.impl.write.ByteWriteFile;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/18
 */
public class ByteAppendFile extends AppendFile<ByteWriteFile, ByteDirectBuffer> {
    public ByteAppendFile(ByteWriteFile writeFile) {
        super(writeFile);
    }

    public void putByte(byte value) {
        writeFile.putByte(lastPos++, value);
    }

    @Override
    protected ByteDirectBuffer newDirectBuf(long address, int size, FileKey fileKey) {
        return new ByteDirectBuf(address, size, fileKey, 1 << (writeFile.connector.getBlockOffset() - writeFile.offset.getOffset()), FileMode.WRITE);
    }
}