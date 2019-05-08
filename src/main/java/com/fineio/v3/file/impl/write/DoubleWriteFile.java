package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class DoubleWriteFile extends WriteFile<DoubleDirectBuffer> {
    public DoubleWriteFile(FileKey fileKey, Connector connector, boolean asyncWrite) {
        super(fileKey, Offset.DOUBLE, connector, asyncWrite);
    }

    public static DoubleWriteFile ofAsync(FileKey fileKey, Connector connector) {
        return new DoubleWriteFile(fileKey, connector, true);
    }

    public static DoubleWriteFile ofSync(FileKey fileKey, Connector connector) {
        return new DoubleWriteFile(fileKey, connector, false);
    }

    public void putDouble(long pos, double value) {
        checkPos(pos);
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        getBuffer(nthBuf).putDouble(nthVal(pos), value);
    }

    @Override
    protected DoubleDirectBuffer getBuffer(int nthBuf) {
        return buffers.computeIfAbsent(nthBuf,
                i -> new DoubleDirectBuf(new FileKey(fileKey.getPath(), String.valueOf(i)),
                        1 << (connector.getBlockOffset() - offset.getOffset())
                ));
    }
}