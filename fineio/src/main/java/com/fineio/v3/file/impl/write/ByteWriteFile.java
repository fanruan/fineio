package com.fineio.v3.file.impl.write;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */

public class ByteWriteFile extends WriteFile<ByteDirectBuffer> {
    public ByteWriteFile(FileBlock fileBlock, Connector connector, boolean asyncWrite) {
        super(fileBlock, Offset.BYTE, connector, asyncWrite);
        buffers = new ByteDirectBuffer[16];
    }

    public static ByteWriteFile ofAsync(FileBlock fileBlock, Connector connector) {
        return new ByteWriteFile(fileBlock, connector, true);
    }

    public static ByteWriteFile ofSync(FileBlock fileBlock, Connector connector) {
        return new ByteWriteFile(fileBlock, connector, false);
    }

    public void putByte(int pos, byte value) {
        ensureOpen();
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        int nthVal = nthVal(pos);
        try {
            buffers[nthBuf].putByte(nthVal, value);
        } catch (NullPointerException e) {
            // buffers[nthBuf]为null，对应写完一个buffer的情况
            newAndPut(nthBuf, nthVal, value);
        } catch (ArrayIndexOutOfBoundsException e) {
            // buffers数组越界，对应当前buffers全写完的情况，也考虑了负下标越界
            growBuffers(nthBuf);
            newAndPut(nthBuf, nthVal, value);
        }
        updateLastPos(pos);
    }

    private void newAndPut(int nthBuf, int nthVal, byte value) {
        buffers[nthBuf] = new ByteDirectBuf(new FileBlock(fileBlock.getPath(), String.valueOf(nthBuf)),
                1 << (blockOffset - offset.getOffset()), FileMode.WRITE);
        buffers[nthBuf].putByte(nthVal, value);
    }

}