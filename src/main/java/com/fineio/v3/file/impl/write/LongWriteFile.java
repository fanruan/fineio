package com.fineio.v3.file.impl.write;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class LongWriteFile extends WriteFile<LongDirectBuffer> {
    public LongWriteFile(FileBlock fileBlock, Connector connector, boolean asyncWrite) {
        super(fileBlock, Offset.LONG, connector, asyncWrite);
        buffers = new LongDirectBuffer[16];
    }

    public static LongWriteFile ofAsync(FileBlock fileBlock, Connector connector) {
        return new LongWriteFile(fileBlock, connector, true);
    }

    public static LongWriteFile ofSync(FileBlock fileBlock, Connector connector) {
        return new LongWriteFile(fileBlock, connector, false);
    }

    public void putLong(int pos, long value) {
        ensureOpen();
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        int nthVal = nthVal(pos);
        try {
            buffers[nthBuf].putLong(nthVal, value);
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

    private void newAndPut(int nthBuf, int nthVal, long value) {
        buffers[nthBuf] = new LongDirectBuf(new FileBlock(fileBlock.getPath(), String.valueOf(nthBuf)),
                1 << (connector.getBlockOffset() - offset.getOffset()), FileMode.WRITE);
        buffers[nthBuf].putLong(nthVal, value);
    }
}