package com.fineio.v3.buffer.impl;

import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/11
 */
public class DoubleDirectBuf extends BaseDirectBuffer implements DoubleDirectBuffer {
    /**
     * for write
     *
     * @param fileKey file key
     */
    public DoubleDirectBuf(FileKey fileKey, Offset offset) {
        super(fileKey, offset);
    }

    /**
     * for read
     *
     * @param address 地址
     * @param cap     容量
     * @param fileKey file key
     */
    public DoubleDirectBuf(long address, int cap, FileKey fileKey, Offset offset) {
        super(address, cap, fileKey, offset);
    }

    @Override
    public void putDouble(int pos, double val) {
        ensureOpen();
        ensureCap(pos);
        checkPos(pos);
        MemoryUtils.put(address, pos, val);
        updateSize(pos);
    }

    @Override
    public double getDouble(int pos) {
        ensureOpen();
        checkPos(pos);
        return MemoryUtils.getDouble(address, pos);
    }
}