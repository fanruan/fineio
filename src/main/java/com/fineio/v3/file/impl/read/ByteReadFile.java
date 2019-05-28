package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class ByteReadFile extends ReadFile<ByteDirectBuffer> {
    public ByteReadFile(FileBlock fileBlock, Connector connector) {
        super(fileBlock, Offset.BYTE, connector);
    }

    public byte getByte(long pos) throws FileClosedException, IllegalArgumentException {
        ensureOpen();
        checkPos(pos);
        return getBuffer(nthBuf(pos)).getByte(nthVal(pos));
    }

    @Override
    ByteDirectBuffer newDirectBuf(long address, int size, FileBlock fileBlock) {
        return new ByteDirectBuf(address, size, fileBlock, size);
    }
}