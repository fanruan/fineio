package com.fineio.io;

import com.fineio.memory.MemoryConstants;

/**
 * Created by daniel on 2017/2/14.
 */
public interface FloatBuffer  extends Buffer {

     int OFFSET = MemoryConstants.OFFSET_FLOAT;

     void put(int position, float b);

     void put(float b);

     float get(int p);

}
