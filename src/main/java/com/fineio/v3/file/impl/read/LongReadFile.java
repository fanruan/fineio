package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.BufferAcquireFailedException;
import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class LongReadFile extends ReadFile<LongDirectBuffer> {
    public LongReadFile(FileBlock fileBlock, Connector connector) {
        super(fileBlock, Offset.LONG, connector);
        init();
    }

    private void init() {
        int lastPos = getLastPos(this);
        buffers = new LongDirectBuffer[nthBuf(lastPos) + 1];
    }

    public long getLong(int pos) {
        ensureOpen();
        int nthBuf = nthBuf(pos);
        int nthVal = nthVal(pos);
        try {
            return buffers[nthBuf].getLong(nthVal);
        } catch (NullPointerException e) {
            // buffers[nthBuf]为null，对应未初始化，从cache拿
            return (buffers[nthBuf] = loadBuffer(nthBuf)).getLong(nthVal);
        } catch (ArrayIndexOutOfBoundsException e) {
            // buffers数组越界，只可能是读到不存在的数据
            throw new BufferAcquireFailedException(fileBlock, e);
        }
    }

    @Override
    LongDirectBuffer newDirectBuf(long address, int size, FileBlock fileBlock) {
        return new LongDirectBuf(address, size, fileBlock, size);
    }
}