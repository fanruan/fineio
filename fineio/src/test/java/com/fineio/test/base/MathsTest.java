package com.fineio.test.base;

import com.fineio.base.Maths;
import junit.framework.TestCase;

/**
 * Created by daniel on 2017/2/21.
 */
public class MathsTest extends TestCase {

    public void testLog2() {
        assertEquals(0, Maths.log2(0));
        assertEquals(0, Maths.log2(1));
        assertEquals(1, Maths.log2(2));
        assertEquals(1, Maths.log2(3));
        assertEquals(2, Maths.log2(4));
        assertEquals(2, Maths.log2(5));
        assertEquals(2, Maths.log2(7));
        assertEquals(3, Maths.log2(8));
        assertEquals(3, Maths.log2(15));
        assertEquals(4, Maths.log2(16));
        assertEquals(4, Maths.log2(17));
        assertEquals(4, Maths.log2(31));
        assertEquals(5, Maths.log2(33));
        assertEquals(0, Maths.log2(-16));
    }
}
