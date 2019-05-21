package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/3
 */

public class ByteWriteFile extends WriteFile<ByteDirectBuffer> {
    public ByteWriteFile(FileKey fileKey, Connector connector, boolean asyncWrite) {
        super(fileKey, Offset.BYTE, connector, asyncWrite);
    }

    public static ByteWriteFile ofAsync(FileKey fileKey, Connector connector) {
        return new ByteWriteFile(fileKey, connector, true);
    }

    public static ByteWriteFile ofSync(FileKey fileKey, Connector connector) {
        return new ByteWriteFile(fileKey, connector, false);
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
                i -> new ByteDirectBuf(new FileKey(fileKey.getPath(), String.valueOf(i)),
                        1 << (connector.getBlockOffset() - offset.getOffset()), FileMode.WRITE));
    }
}