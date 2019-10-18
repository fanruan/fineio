package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.BufferAcquireFailedException;
import com.fineio.v3.buffer.BufferClosedException;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.buffer.impl.safe.SafeByteDirectBuf;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class ByteReadFile extends ReadFile<ByteDirectBuffer> {
    public ByteReadFile(FileBlock fileBlock, Connector connector) {
        super(fileBlock, Offset.BYTE, connector);
        init();
    }

    private void init() {
        int lastPos = initMetaAndGetLastPos();
        buffers = new ByteDirectBuffer[nthBuf(lastPos) + 1];
    }

    public byte getByte(int pos) {
        ensureOpen();
        int nthBuf = nthBuf(pos);
        int nthVal = nthVal(pos);
        try {
            return buffers[nthBuf].getByte(nthVal);
        } catch (NullPointerException | BufferClosedException e) {
            // buffers[nthBuf]为null，对应未初始化，从cache拿
            // 被缓存close掉，重新load
            return (buffers[nthBuf] = loadBuffer(nthBuf)).getByte(nthVal);
        } catch (ArrayIndexOutOfBoundsException e) {
            // buffers数组越界，只可能是读到不存在的数据
            throw new BufferAcquireFailedException(fileBlock, e);
        }
    }

    @Override
    ByteDirectBuffer newDirectBuf(long address, int size, FileBlock fileBlock) {
        return new SafeByteDirectBuf(new ByteDirectBuf(address, size, fileBlock, size));
    }
}