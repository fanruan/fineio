package com.fineio.v3.file.impl.write;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class DoubleWriteFile extends WriteFile<DoubleDirectBuffer> {
    public DoubleWriteFile(FileBlock FileBlock, Connector connector, boolean asyncWrite) {
        super(FileBlock, Offset.DOUBLE, connector, asyncWrite);
    }

    public static DoubleWriteFile ofAsync(FileBlock FileBlock, Connector connector) {
        return new DoubleWriteFile(FileBlock, connector, true);
    }

    public static DoubleWriteFile ofSync(FileBlock FileBlock, Connector connector) {
        return new DoubleWriteFile(FileBlock, connector, false);
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