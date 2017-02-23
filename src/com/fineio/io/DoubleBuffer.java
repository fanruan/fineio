package com.fineio.io;

import com.fineio.memory.MemoryConstants;

/**
 * Created by daniel on 2017/2/14.
 */
public interface DoubleBuffer extends Buffer {

     int OFFSET = MemoryConstants.OFFSET_DOUBLE;


     void put(int position, double b);


     double get(int p);

}
