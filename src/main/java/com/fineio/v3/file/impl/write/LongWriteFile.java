package com.fineio.v3.file.impl.write;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class LongWriteFile extends WriteFile<LongDirectBuffer> {
    public LongWriteFile(FileBlock fileBlock, Connector connector, boolean asyncWrite) {
        super(fileBlock, Offset.LONG, connector, asyncWrite);
    }

    public static LongWriteFile ofAsync(FileBlock fileBlock, Connector connector) {
        return new LongWriteFile(fileBlock, connector, true);
    }

    public static LongWriteFile ofSync(FileBlock fileBlock, Connector connector) {
        return new LongWriteFile(fileBlock, connector, false);
    }

    public void putLong(long pos, long value) throws FileClosedException, IllegalArgumentException {
        ensureOpen();
        checkPos(pos);
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        getBuffer(nthBuf).putLong(nthVal(pos), value);
    }

    @Override
    protected LongDirectBuffer getBuffer(int nthBuf) {
        return buffers.computeIfAbsent(nthBuf,
                i -> new LongDirectBuf(new FileBlock(fileBlock.getPath(), String.valueOf(i)),
                        1 << (connector.getBlockOffset() - offset.getOffset()), FileMode.WRITE));
    }
}