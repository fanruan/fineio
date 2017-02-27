package com.fineio.io;

import com.fineio.memory.MemoryConstants;

/**
 * Created by daniel on 2017/2/14.
 */
public interface ShortBuffer extends Buffer {

    int OFFSET = MemoryConstants.OFFSET_SHORT;

    void put(int position, short b);

    void put(short b);

    short get(int p);

}
