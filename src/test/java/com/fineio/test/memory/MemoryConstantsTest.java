package com.fineio.test.memory;

import junit.framework.TestCase;

import static com.fineio.memory.MemoryConstants.*;


/**
 * Created by daniel on 2017/2/10.
 */
public class MemoryConstantsTest extends TestCase{

    public void testConstants(){
        assertEquals(OFFSET_BYTE, 0);
        assertEquals(OFFSET_CHAR, 1);
        assertEquals(OFFSET_SHORT, 1);
        assertEquals(OFFSET_INT, 2);
        assertEquals(OFFSET_FLOAT, 2);
        assertEquals(OFFSET_LONG, 3);
        assertEquals(OFFSET_DOUBLE, 3);
        assertEquals(STEP_BYTE, 1);
        assertEquals(STEP_CHAR, 2);
        assertEquals(STEP_SHORT, 2);
        assertEquals(STEP_INT, 4);
        assertEquals(STEP_FLOAT, 4);
        assertEquals(STEP_LONG, 8);
        assertEquals(STEP_DOUBLE, 8);
    }

}
