package com.fineio.v3.buffer.impl;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;

/**
 * @author anchore
 * @date 2019/4/11
 */
public class ByteDirectBuf extends BaseDirectBuffer implements ByteDirectBuffer {
    /**
     * for write
     *
     * @param fileBlock file key
     */
    public ByteDirectBuf(FileBlock fileBlock, int maxCap, FileMode fileMode) {
        super(fileBlock, Offset.BYTE, maxCap, fileMode);
    }

    /**
     * for read or append
     *
     * @param address   地址
     * @param cap       容量
     * @param fileBlock file key
     */
    public ByteDirectBuf(long address, int cap, FileBlock fileBlock, int maxCap) {
        super(address, cap, fileBlock, Offset.BYTE, maxCap);
    }

    @Override
    public void putByte(int pos, byte val) {
        ensureOpen();
        ensureCap(pos);
        checkPos(pos);
        MemoryUtils.put(address, pos, val);
        updateSize(pos);
    }

    @Override
    public byte getByte(int pos) {
        // 不用ensureOpen是因为有safe buffer资瓷
        checkPos(pos);
        return MemoryUtils.getByte(address, pos);
    }
}