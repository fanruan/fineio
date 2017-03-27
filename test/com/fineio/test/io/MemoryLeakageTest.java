package com.fineio.test.io;

import com.fineio.FineIO;
import junit.framework.TestCase;

/**
 * Created by daniel on 2017/3/27.
 */
public class MemoryLeakageTest extends TestCase {


    public static void assertZeroMemory(){
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testMemoryLeak() {
        assertZeroMemory();





    }
}
