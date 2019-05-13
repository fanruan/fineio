package com.fineio.v3.file.impl;

import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.file.impl.write.LongWriteFile;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/18
 */
public class LongAppendFile extends AppendFile<LongWriteFile, LongDirectBuffer> {
    public LongAppendFile(LongWriteFile writeFile) {
        super(writeFile);
    }

    public void putLong(long value) {
        writeFile.putLong(lastPos++, value);
    }

    @Override
    protected LongDirectBuffer newDirectBuf(long address, int size, FileKey fileKey) {
        return new LongDirectBuf(address, size, fileKey, 1 << (writeFile.connector.getBlockOffset() - writeFile.offset.getOffset()), FileMode.APPEND);
    }
}