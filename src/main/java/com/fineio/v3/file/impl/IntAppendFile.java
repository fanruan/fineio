package com.fineio.v3.file.impl;

import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.buffer.impl.IntDirectBuf;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.file.impl.write.IntWriteFile;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/18
 */
public class IntAppendFile extends AppendFile<IntWriteFile, IntDirectBuffer> {
    public IntAppendFile(IntWriteFile writeFile) {
        super(writeFile);
    }

    public void putInt(int value) {
        writeFile.putInt(lastPos++, value);
    }

    @Override
    protected IntDirectBuffer newDirectBuf(long address, int size, FileKey fileKey) {
        return new IntDirectBuf(address, size, fileKey, 1 << (writeFile.connector.getBlockOffset() - writeFile.offset.getOffset()), FileMode.WRITE);
    }
}