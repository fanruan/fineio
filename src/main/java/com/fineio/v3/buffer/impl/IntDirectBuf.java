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
     * for read
     *
     * @param address   地址
     * @param cap       容量
     * @param fileBlock file key
     */
    public IntDirectBuf(long address, int cap, FileBlock fileBlock, int maxCap) {
        super(address, cap, fileBlock, Offset.INT, maxCap);
    }

    /**
     * for overwrite
     *
     * @param fileBlock file key
     */
    public IntDirectBuf(FileBlock fileBlock, int maxCap, FileMode fileMode) {
        super(fileBlock, Offset.INT, maxCap, fileMode);
    }

    /**
     * for append
     *
     * @param fileBlock file key
     */
    public IntDirectBuf(long address, int cap, int maxCap, FileBlock fileBlock) {
        super(address, cap, maxCap, fileBlock, Offset.INT);
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
        // 不用ensureOpen是因为有safe buffer资瓷
        checkPos(pos);
        return MemoryUtils.getInt(address, pos);
    }
}