package com.fineio.io;

import com.fineio.memory.MemoryConstants;

/**
 * Created by daniel on 2017/2/14.
 */
public  interface LongBuffer   extends Buffer{
    int OFFSET = MemoryConstants.OFFSET_LONG;

    void put(int position, long b);

    void put(long b);

    long get(int p);
}
