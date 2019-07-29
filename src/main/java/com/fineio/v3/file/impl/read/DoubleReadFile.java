package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.BufferAcquireFailedException;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.buffer.impl.guard.SafeDoubleDirectBuf;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class DoubleReadFile extends ReadFile<DoubleDirectBuffer> {
    public DoubleReadFile(FileBlock fileBlock, Connector connector) {
        super(fileBlock, Offset.DOUBLE, connector);
        init();
    }

    private void init() {
        int lastPos = getLastPos(this);
        buffers = new DoubleDirectBuffer[nthBuf(lastPos) + 1];
    }

    public double getDouble(int pos) {
        ensureOpen();
        int nthBuf = nthBuf(pos);
        int nthVal = nthVal(pos);
        try {
            return buffers[nthBuf].getDouble(nthVal);
        } catch (NullPointerException e) {
            // buffers[nthBuf]为null，对应未初始化，从cache拿
            return (buffers[nthBuf] = loadBuffer(nthBuf)).getDouble(nthVal);
        } catch (ArrayIndexOutOfBoundsException e) {
            // buffers数组越界，只可能是读到不存在的数据
            throw new BufferAcquireFailedException(fileBlock, e);
        }
    }

    @Override
    DoubleDirectBuffer newDirectBuf(long address, int size, FileBlock fileBlock) {
        return new SafeDoubleDirectBuf(new DoubleDirectBuf(address, size, fileBlock, size));
    }
}