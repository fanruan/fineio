package com.fineio.v3.file.impl.write;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */

public class ByteWriteFile extends WriteFile<ByteDirectBuffer> {
    public ByteWriteFile(FileBlock fileBlock, Connector connector, boolean asyncWrite) {
        super(fileBlock, Offset.BYTE, connector, asyncWrite);
    }

    public static ByteWriteFile ofAsync(FileBlock fileBlock, Connector connector) {
        return new ByteWriteFile(fileBlock, connector, true);
    }

    public static ByteWriteFile ofSync(FileBlock fileBlock, Connector connector) {
        return new ByteWriteFile(fileBlock, connector, false);
    }

    public void putByte(long pos, byte value) throws FileClosedException, IllegalArgumentException {
        ensureOpen();
        checkPos(pos);
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        getBuffer(nthBuf).putByte(nthVal(pos), value);
    }

    @Override
    protected ByteDirectBuffer getBuffer(int nthBuf) {
        return buffers.computeIfAbsent(nthBuf,
                i -> new ByteDirectBuf(new FileBlock(fileBlock.getPath(), String.valueOf(i)),
                        1 << (connector.getBlockOffset() - offset.getOffset()), FileMode.WRITE));
    }
}