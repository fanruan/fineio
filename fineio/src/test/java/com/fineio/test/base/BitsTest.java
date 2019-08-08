package com.fineio.test.base;

import com.fineio.base.Bits;
import com.fineio.memory.MemoryUtils;
import junit.framework.TestCase;

/**
 * Created by daniel on 2017/2/10.
 */
public class BitsTest extends TestCase {

    public void testAll() throws Exception {
        for (int i = 0; i < 10000; i++) {
            allTest();
            testBitsAndUnsafe();
        }
    }


    public void testBitsAndUnsafe() {
        double d = Math.random() * 1000000000d;
        byte[] b = new byte[8];
        Bits.putDouble(b, 0, d);
        long s = MemoryUtils.allocate(8);
        putMemory(b, s);
        assertEquals(MemoryUtils.getDouble(s, 0), d);

        long l = (long) d;
        Bits.putLong(b, 0, l);
        putMemory(b, s);
        assertEquals(MemoryUtils.getLong(s, 0), l);

        float f = (float) d;
        Bits.putFloat(b, 0, f);
        putMemory(b, s);
        assertEquals(MemoryUtils.getFloat(s, 0), f);

        int i = (int) d;
        Bits.putInt(b, 0, i);
        putMemory(b, s);
        assertEquals(MemoryUtils.getInt(s, 0), i);

        char c = (char) d;
        Bits.putInt(b, 0, c);
        putMemory(b, s);
        assertEquals(MemoryUtils.getChar(s, 0), c);

        short sh = (short) d;
        Bits.putInt(b, 0, sh);
        putMemory(b, s);
        assertEquals(MemoryUtils.getShort(s, 0), sh);

        boolean bool = d > 1000000;
        Bits.putBoolean(b, 0, bool);
        putMemory(b, s);
        assertEquals(MemoryUtils.getByte(s, 0) == 1, bool);
        MemoryUtils.free(s);
    }

    private void putMemory(byte[] b, long s) {
        for (int i = 0; i < b.length; i++) {
            MemoryUtils.put(s, i, b[i]);
        }
    }


    public void allTest() {
        double d = Math.random() * 1000000000d;
        byte[] b = new byte[8];
        Bits.putDouble(b, 0, d);
        assertEquals(Bits.getDouble(b, 0), d);

        long l = (long) d;
        Bits.putLong(b, 0, l);
        assertEquals(Bits.getLong(b, 0), l);

        float f = (float) d;
        Bits.putFloat(b, 0, f);
        assertEquals(Bits.getFloat(b, 0), f);

        int i = (int) d;
        Bits.putInt(b, 0, i);
        assertEquals(Bits.getInt(b, 0), i);

        char c = (char) d;
        Bits.putChar(b, 0, c);
        assertEquals(Bits.getChar(b, 0), c);

        short s = (short) d;
        Bits.putShort(b, 0, s);
        assertEquals(Bits.getShort(b, 0), s);

        boolean bool = d > 1000000;
        Bits.putBoolean(b, 0, bool);
        assertEquals(Bits.getBoolean(b, 0), bool);
    }
}
