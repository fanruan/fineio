package com.fineio.v1.io;

import com.fineio.memory.MemoryConstants;

/**
 * Created by daniel on 2017/2/14.
 */
public interface IntBuffer   extends Buffer {
    int OFFSET = MemoryConstants.OFFSET_INT;


    void put(int position, int b);

    void put(int b);

    int get(int p);
}

