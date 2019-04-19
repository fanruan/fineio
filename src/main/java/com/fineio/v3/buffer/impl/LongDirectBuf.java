package com.fineio.v3.buffer.impl;

import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/11
 */
public class LongDirectBuf extends BaseDirectBuffer implements LongDirectBuffer {
    /**
     * for write
     *
     * @param fileKey file key
     */
    public LongDirectBuf(FileKey fileKey, int maxCap) {
        super(fileKey, Offset.LONG, maxCap);
    }

    /**
     * for read
     *
     * @param address 地址
     * @param cap     容量
     * @param fileKey file key
     */
    public LongDirectBuf(long address, int cap, FileKey fileKey) {
        super(address, cap, fileKey, Offset.LONG);
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