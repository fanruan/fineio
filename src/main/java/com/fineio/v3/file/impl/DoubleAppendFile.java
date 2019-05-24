package com.fineio.v3.file.impl;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.file.impl.write.DoubleWriteFile;

/**
 * @author anchore
 * @date 2019/4/18
 */
public class DoubleAppendFile extends AppendFile<DoubleWriteFile, DoubleDirectBuffer> {
    public DoubleAppendFile(DoubleWriteFile writeFile) {
        super(writeFile);
    }

    public void putDouble(double value) {
        writeFile.putDouble(lastPos++, value);
    }

    @Override
    protected DoubleDirectBuffer newDirectBuf(long address, int size, FileBlock fileBlock) {
        return new DoubleDirectBuf(address, size, fileBlock, 1 << (writeFile.connector.getBlockOffset() - writeFile.offset.getOffset()), FileMode.WRITE);
    }
}