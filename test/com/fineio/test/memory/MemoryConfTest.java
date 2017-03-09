package com.fineio.test.memory;

import com.fineio.FineIO;
import com.fineio.exception.MemorySetException;
import com.fineio.memory.MemoryConf;
import junit.framework.TestCase;

/**
 * Created by daniel on 2017/2/13.
 */
public class MemoryConfTest extends TestCase {

    public void testGetAndSet(){
        assertEquals(MemoryConf.getMinMemSizeForSet(), 1L<<30);
        long s = FineIO.getTotalMemSize();
        assertTrue(s >= FineIO.getMinMemSizeForSet());
        boolean exp = false;
        try {
            FineIO.setTotalMemSize(100);
        } catch (MemorySetException e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        try {
            FineIO.setTotalMemSize(Long.MAX_VALUE);
        } catch (MemorySetException e) {
            exp = true;
        }
        assertTrue(exp);
        assertTrue(s >= MemoryConf.getMinMemSizeForSet());
    }
}
