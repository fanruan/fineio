package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class LongWriteFile extends WriteFile<LongDirectBuffer> {
    public LongWriteFile(FileKey fileKey, Connector connector, boolean asyncWrite) {
        super(fileKey, Offset.LONG, connector, asyncWrite);
    }

    public static LongWriteFile ofAsync(FileKey fileKey, Connector connector) {
        return new LongWriteFile(fileKey, connector, true);
    }

    public static LongWriteFile ofSync(FileKey fileKey, Connector connector) {
        return new LongWriteFile(fileKey, connector, false);
    }

    public void putLong(long pos, long value) {
        checkPos(pos);
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        getBuffer(nthBuf).putLong(nthVal(pos), value);
    }

    @Override
    protected LongDirectBuffer getBuffer(int nthBuf) {
        return buffers.computeIfAbsent(nthBuf,
                i -> new LongDirectBuf(new FileKey(fileKey.getPath(), String.valueOf(i)),
                        1 << (connector.getBlockOffset() - offset.getOffset())
                ));
    }
}