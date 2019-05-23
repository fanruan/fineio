package com.fineio.v3.file.impl.write;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.buffer.impl.IntDirectBuf;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class IntWriteFile extends WriteFile<IntDirectBuffer> {
    public IntWriteFile(FileBlock FileBlock, Connector connector, boolean asyncWrite) {
        super(FileBlock, Offset.INT, connector, asyncWrite);
    }

    public static IntWriteFile ofAsync(FileBlock FileBlock, Connector connector) {
        return new IntWriteFile(FileBlock, connector, true);
    }

    public static IntWriteFile ofSync(FileBlock FileBlock, Connector connector) {
        return new IntWriteFile(FileBlock, connector, false);
    }

    public void putInt(long pos, int value) throws FileClosedException, IllegalArgumentException {
        ensureOpen();
        checkPos(pos);
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        getBuffer(nthBuf).putInt(nthVal(pos), value);
    }

    @Override
    protected IntDirectBuffer getBuffer(int nthBuf) {
        return buffers.computeIfAbsent(nthBuf,
                i -> new IntDirectBuf(new FileBlock(fileBlock.getPath(), String.valueOf(i)),
                        1 << (connector.getBlockOffset() - offset.getOffset()), FileMode.WRITE));
    }
}