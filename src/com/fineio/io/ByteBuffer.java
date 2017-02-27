package com.fineio.io;

import com.fineio.memory.MemoryConstants;

/**
 * Created by daniel on 2017/2/9.
 */
public  interface ByteBuffer extends Buffer {

    int OFFSET = MemoryConstants.OFFSET_BYTE;

    void put(int position, byte b);

    void put(byte b);

    byte get(int p);

}