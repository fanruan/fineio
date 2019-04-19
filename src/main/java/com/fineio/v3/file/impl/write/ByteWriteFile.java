package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */

public class ByteWriteFile extends WriteFile<ByteDirectBuffer> {
    public ByteWriteFile(FileKey fileKey, Connector connector) {
        super(fileKey, Offset.BYTE, connector);
    }

    public void putByte(long pos, byte value) {
        checkPos(pos);
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        getBuffer(nthBuf).putByte(nthVal(pos), value);
    }

    @Override
    protected ByteDirectBuffer getBuffer(int nthBuf) {
        return buffers.computeIfAbsent(nthBuf,
                i -> new ByteDirectBuf(new FileKey(fileKey.getPath(), String.valueOf(i)),
                        1 << (connector.getBlockOffset() - offset.getOffset())
                ));
    }
}