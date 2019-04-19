package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.buffer.impl.IntDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class IntWriteFile extends WriteFile<IntDirectBuffer> {
    public IntWriteFile(FileKey fileKey, Connector connector) {
        super(fileKey, Offset.INT, connector);
    }

    public void putInt(long pos, int value) {
        checkPos(pos);
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        getBuffer(nthBuf).putInt(nthVal(pos), value);
    }

    @Override
    protected IntDirectBuffer getBuffer(int nthBuf) {
        return buffers.computeIfAbsent(nthBuf,
                i -> new IntDirectBuf(new FileKey(fileKey.getPath(), String.valueOf(i)),
                        1 << (connector.getBlockOffset() - offset.getOffset())
                ));
    }
}