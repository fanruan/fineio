package com.fineio.v3.file.impl.write;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class DoubleWriteFile extends WriteFile<DoubleDirectBuffer> {
    public DoubleWriteFile(FileBlock fileBlock, Connector connector, boolean asyncWrite) {
        super(fileBlock, Offset.DOUBLE, connector, asyncWrite);
    }

    public static DoubleWriteFile ofAsync(FileBlock fileBlock, Connector connector) {
        return new DoubleWriteFile(fileBlock, connector, true);
    }

    public static DoubleWriteFile ofSync(FileBlock fileBlock, Connector connector) {
        return new DoubleWriteFile(fileBlock, connector, false);
    }

    public void putDouble(long pos, double value) throws FileClosedException, IllegalArgumentException {
        ensureOpen();
        checkPos(pos);
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        getBuffer(nthBuf).putDouble(nthVal(pos), value);
    }

    @Override
    protected DoubleDirectBuffer getBuffer(int nthBuf) {
        return buffers.computeIfAbsent(nthBuf,
                i -> new DoubleDirectBuf(new FileBlock(fileBlock.getPath(), String.valueOf(i)),
                        1 << (connector.getBlockOffset() - offset.getOffset()), FileMode.WRITE));
    }
}