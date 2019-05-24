package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class DoubleReadFile extends ReadFile<DoubleDirectBuffer> {
    public DoubleReadFile(FileBlock fileBlock, Connector connector) {
        super(fileBlock, Offset.DOUBLE, connector);
    }

    public double getDouble(long pos) throws FileClosedException, IllegalArgumentException {
        ensureOpen();
        checkPos(pos);
        return getBuffer(nthBuf(pos)).getDouble(nthVal(pos));
    }

    @Override
    DoubleDirectBuffer newDirectBuf(long address, int size, FileBlock fileBlock) {
        return new DoubleDirectBuf(address, size, fileBlock, size, FileMode.READ);
    }
}