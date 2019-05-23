package com.fineio.v3.buffer.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/11
 */
public class LongDirectBuf extends BaseDirectBuffer implements LongDirectBuffer {
    /**
     * for write
     *
     * @param fileBlock file key
     */
    public LongDirectBuf(FileBlock fileBlock, int maxCap, FileMode fileMode) {
        super(fileBlock, Offset.LONG, maxCap, fileMode);
    }

    /**
     * for read
     *
     * @param address 地址
     * @param cap     容量
     * @param fileBlock file key
     */
    public LongDirectBuf(long address, int cap, FileBlock fileBlock, int maxCap, FileMode fileMode) {
        super(address, cap, fileBlock, Offset.LONG, maxCap, fileMode);
    }

    @Override
    public void putLong(int pos, long val) {
        ensureOpen();
        ensureCap(pos);
        checkPos(pos);
        MemoryUtils.put(address, pos, val);
        updateSize(pos);
    }

    @Override
    public long getLong(int pos) {
        ensureOpen();
        checkPos(pos);
        return MemoryUtils.getLong(address, pos);
    }
}