package com.fineio.test.memory;

import junit.framework.TestCase;

import static com.fineio.memory.MemoryConstants.OFFSET_BYTE;
import static com.fineio.memory.MemoryConstants.OFFSET_CHAR;
import static com.fineio.memory.MemoryConstants.OFFSET_DOUBLE;
import static com.fineio.memory.MemoryConstants.OFFSET_FLOAT;
import static com.fineio.memory.MemoryConstants.OFFSET_INT;
import static com.fineio.memory.MemoryConstants.OFFSET_LONG;
import static com.fineio.memory.MemoryConstants.OFFSET_SHORT;
import static com.fineio.memory.MemoryConstants.STEP_BYTE;
import static com.fineio.memory.MemoryConstants.STEP_CHAR;
import static com.fineio.memory.MemoryConstants.STEP_DOUBLE;
import static com.fineio.memory.MemoryConstants.STEP_FLOAT;
import static com.fineio.memory.MemoryConstants.STEP_INT;
import static com.fineio.memory.MemoryConstants.STEP_LONG;
import static com.fineio.memory.MemoryConstants.STEP_SHORT;


/**
 * Created by daniel on 2017/2/10.
 */
public class MemoryConstantsTest extends TestCase {

    public void testConstants() {
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
