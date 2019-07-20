package com.fineio.v3.file.impl.write;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/3
 */
public class DoubleWriteFile extends WriteFile<DoubleDirectBuffer> {
    public DoubleWriteFile(FileBlock fileBlock, Connector connector, boolean asyncWrite) {
        super(fileBlock, Offset.DOUBLE, connector, asyncWrite);
        buffers = new DoubleDirectBuffer[16];
    }

    public static DoubleWriteFile ofAsync(FileBlock fileBlock, Connector connector) {
        return new DoubleWriteFile(fileBlock, connector, true);
    }

    public static DoubleWriteFile ofSync(FileBlock fileBlock, Connector connector) {
        return new DoubleWriteFile(fileBlock, connector, false);
    }

    public void putDouble(int pos, double value) {
        ensureOpen();
        int nthBuf = nthBuf(pos);
        syncBufIfNeed(nthBuf);
        int nthVal = nthVal(pos);
        try {
            buffers[nthBuf].putDouble(nthVal, value);
        } catch (NullPointerException e) {
            // buffers[nthBuf]为null，对应写完一个buffer的情况
            newAndPut(nthBuf, nthVal, value);
        } catch (ArrayIndexOutOfBoundsException e) {
            // buffers数组越界，对应当前buffers全写完的情况，也考虑了负下标越界
            growBufferCache(nthBuf);
            newAndPut(nthBuf, nthVal, value);
        }
        updateLastPos(pos);
    }

    private void newAndPut(int nthBuf, int nthVal, double value) {
        buffers[nthBuf] = new DoubleDirectBuf(new FileBlock(fileBlock.getPath(), String.valueOf(nthBuf)),
                1 << (connector.getBlockOffset() - offset.getOffset()), FileMode.WRITE);
        buffers[nthBuf].putDouble(nthVal, value);
    }

}