package com.fineio.v3.file.impl;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.file.impl.write.LongWriteFile;

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
    protected LongDirectBuffer newDirectBuf(long address, int size, FileBlock fileBlock) {
        return new LongDirectBuf(address, size, fileBlock, 1 << (writeFile.connector.getBlockOffset() - writeFile.offset.getOffset()), FileMode.WRITE);
    }
}