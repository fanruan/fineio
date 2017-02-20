package com.fineio.io.write;

import com.fineio.file.FileBlock;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/9.
 */
public class ByteWriteBuffer extends  WriteBuffer {

    public static final int OFFSET = MemoryConstants.OFFSET_BYTE;

    private ByteWriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }

    public  final  void put(byte b) {
        ensureCapacity();
        MemoryUtils.put(address, position++, b);
    }

}