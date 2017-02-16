package com.fineio.io.read;

import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by daniel on 2017/2/9.
 */
public class ByteReadBuffer extends  ReadBuffer {

    public static final int OFFSET = MemoryConstants.OFFSET_BYTE;

    private ByteReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final byte get(int p) {
        checkIndex(p);
        return MemoryUtils.getByte(address, p);
    }

}