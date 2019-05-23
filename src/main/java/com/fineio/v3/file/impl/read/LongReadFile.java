package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class LongReadFile extends ReadFile<LongDirectBuffer> {
    public LongReadFile(FileBlock FileBlock, Connector connector) {
        super(FileBlock, Offset.LONG, connector);
    }

    public long getLong(long pos) throws FileClosedException, IllegalArgumentException {
        ensureOpen();
        checkPos(pos);
        return getBuffer(nthBuf(pos)).getLong(nthVal(pos));
    }

    @Override
    LongDirectBuffer newDirectBuf(long address, int size, FileBlock FileBlock) {
        return new LongDirectBuf(address, size, FileBlock, size, FileMode.READ);
    }
}