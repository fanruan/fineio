package com.fineio.v1.io;

import com.fineio.memory.MemoryConstants;

/**
 * Created by daniel on 2017/2/14.
 */
public interface CharBuffer extends Buffer {

    int OFFSET = MemoryConstants.OFFSET_CHAR;

    void put(int position, char b);

    void put(char b);

    char get(int p);
}
