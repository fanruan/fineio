package com.fineio.v3.buffer.impl;

import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/11
 */
public class IntDirectBuf extends BaseDirectBuffer implements IntDirectBuffer {
    /**
     * for write
     *
     * @param fileKey file key
     */
    public IntDirectBuf(FileKey fileKey, int maxCap) {
        super(fileKey, Offset.INT, maxCap);
    }

    /**
     * for read
     *
     * @param address 地址
     * @param cap     容量
     * @param fileKey file key
     */
    public IntDirectBuf(long address, int cap, FileKey fileKey, int maxCap) {
        super(address, cap, fileKey, Offset.INT, maxCap);
    }

    @Override
    public void putInt(int pos, int val) {
        ensureOpen();
        ensureCap(pos);
        checkPos(pos);
        MemoryUtils.put(address, pos, val);
        updateSize(pos);
    }

    @Override
    public int getInt(int pos) {
        ensureOpen();
        checkPos(pos);
        return MemoryUtils.getInt(address, pos);
    }
}