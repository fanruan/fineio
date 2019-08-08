package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.BufferAcquireFailedException;
import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.buffer.impl.IntDirectBuf;
import com.fineio.v3.buffer.impl.safe.SafeIntDirectBuf;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class IntReadFile extends ReadFile<IntDirectBuffer> {
    public IntReadFile(FileBlock fileBlock, Connector connector) {
        super(fileBlock, Offset.INT, connector);
        init();
    }

    private void init() {
        int lastPos = initMetaAndGetLastPos(this);
        buffers = new IntDirectBuffer[nthBuf(lastPos) + 1];
    }

    public int getInt(int pos) {
        ensureOpen();
        int nthBuf = nthBuf(pos);
        int nthVal = nthVal(pos);
        try {
            return buffers[nthBuf].getInt(nthVal);
        } catch (NullPointerException e) {
            // buffers[nthBuf]为null，对应未初始化，从cache拿
            return (buffers[nthBuf] = loadBuffer(nthBuf)).getInt(nthVal);
        } catch (ArrayIndexOutOfBoundsException e) {
            // buffers数组越界，只可能是读到不存在的数据
            throw new BufferAcquireFailedException(fileBlock, e);
        }
    }

    @Override
    IntDirectBuffer newDirectBuf(long address, int size, FileBlock fileBlock) {
        return new SafeIntDirectBuf(new IntDirectBuf(address, size, fileBlock, size));
    }
}