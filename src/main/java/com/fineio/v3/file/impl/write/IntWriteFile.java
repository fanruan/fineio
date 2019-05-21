package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.buffer.impl.IntDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class IntWriteFile extends WriteFile<IntDirectBuffer> {
    public IntWriteFile(FileKey fileKey, Connector connector, boolean asyncWrite) {
        super(fileKey, Offset.INT, connector, asyncWrite);
    }

    public static IntWriteFile ofAsync(FileKey fileKey, Connector connector) {
        return new IntWriteFile(fileKey, connector, true);
    }

    public static IntWriteFile ofSync(FileKey fileKey, Connector connector) {
        return new IntWriteFile(fileKey, connector, false);
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
                i -> new IntDirectBuf(new FileKey(fileKey.getPath(), String.valueOf(i)),
                        1 << (connector.getBlockOffset() - offset.getOffset()), FileMode.WRITE));
    }
}