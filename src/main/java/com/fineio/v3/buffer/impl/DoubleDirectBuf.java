package com.fineio.v3.buffer.impl;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.DoubleDirectBuffer;
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
     * @param fileBlock file key
     */
    public DoubleDirectBuf(FileBlock fileBlock, int maxCap, FileMode fileMode) {
        super(fileBlock, Offset.DOUBLE, maxCap, fileMode);
    }

    /**
     * for read
     *
     * @param address 地址
     * @param cap     容量
     * @param fileBlock file key
     */
    public DoubleDirectBuf(long address, int cap, FileBlock fileBlock, int maxCap) {
        super(address, cap, fileBlock, Offset.DOUBLE, maxCap);
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