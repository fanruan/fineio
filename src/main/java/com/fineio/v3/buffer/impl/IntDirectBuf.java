package com.fineio.v3.buffer.impl;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.IntDirectBuffer;
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
     * @param fileBlock file key
     */
    public IntDirectBuf(FileBlock fileBlock, int maxCap, FileMode fileMode) {
        super(fileBlock, Offset.INT, maxCap, fileMode);
    }

    /**
     * for read
     *
     * @param address 地址
     * @param cap     容量
     * @param fileBlock file key
     */
    public IntDirectBuf(long address, int cap, FileBlock fileBlock, int maxCap, FileMode fileMode) {
        super(address, cap, fileBlock, Offset.INT, maxCap, fileMode);
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