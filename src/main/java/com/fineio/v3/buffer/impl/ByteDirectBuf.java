package com.fineio.v3.buffer.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

/**
 * @author anchore
 * @date 2019/4/11
 */
public class ByteDirectBuf extends BaseDirectBuffer implements ByteDirectBuffer {
    /**
     * for write
     *
     * @param FileBlock file key
     */
    public ByteDirectBuf(FileBlock FileBlock, int maxCap, FileMode fileMode) {
        super(FileBlock, Offset.BYTE, maxCap, fileMode);
    }

    /**
     * for read
     *
     * @param address 地址
     * @param cap     容量
     * @param FileBlock file key
     */
    public ByteDirectBuf(long address, int cap, FileBlock FileBlock, int maxCap, FileMode fileMode) {
        super(address, cap, FileBlock, Offset.BYTE, maxCap, fileMode);
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
        ensureOpen();
        checkPos(pos);
        return MemoryUtils.getByte(address, pos);
    }
}