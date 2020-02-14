package com.fineio.v3.memory;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author yee
 * @date 2019-04-11
 */
public class MemoryUtilsTest extends TestCase {

    public void testCopyMemory() {
        byte[] bytes = createRandomByte();
        int len = bytes.length;
        long address = MemoryUtils.allocate(len);
        MemoryUtils.copyMemory(bytes, address);
        for (int i = 0; i < len; i++) {
            assertEquals(bytes[i], MemoryUtils.getByte(address, i));
        }
    }

    public void testCopyMemoryLen() {
        byte[] bytes = createRandomByte();
        int len = bytes.length;
        long address = MemoryUtils.allocate(len);
        MemoryUtils.copyMemory(bytes, address, len / 2);
        for (int i = 0; i < len / 2; i++) {
            assertEquals(bytes[i], MemoryUtils.getByte(address, i));
        }
    }

    public void testReadMemory() {
        byte[] bytes = createRandomByte();
        int len = bytes.length;
        long address = MemoryUtils.allocate(len);
        MemoryUtils.copyMemory(bytes, address);

        byte[] res = new byte[len];
        MemoryUtils.readMemory(res, address, len);
        for (int i = 0; i < len; i++) {
            assertEquals(res[i], bytes[i]);
        }
        byte[] res2 = new byte[len];
        MemoryUtils.copyMemory(res, res2);
        for (int i = 0; i < len; i++) {
            assertEquals(res2[i], bytes[i]);
        }
        byte[] res3 = new byte[len / 2];
        MemoryUtils.copyMemory(res2, res3);
        for (int i = 0; i < res3.length; i++) {
            assertEquals(res3[i], bytes[i]);
        }

        byte[] res4 = new byte[len * 2];
        MemoryUtils.copyMemory(res2, res4, len);
        for (int i = 0; i < res2.length; i++) {
            assertEquals(res4[i], bytes[i]);
        }
        for (int i = res2.length; i < res4.length; i++) {
            assertEquals(res4[i], 0);
        }
    }

    public void testReadMemory2() {
        byte[] bytes = createRandomByte();
        while (bytes.length < 50) {
            bytes = createRandomByte();
        }
        int len = bytes.length;
        long address = MemoryUtils.allocate(len);
        MemoryUtils.copyMemory(bytes, address);
        byte[] res = new byte[300];
        int minLen = Math.min(100, len);
        MemoryUtils.readMemory(res, 100, address, minLen);
        for (int i = 0; i < 100; i++) {
            assertEquals(res[i], 0);
        }
        for (int i = 100; i < 100 + minLen; i++) {
            assertEquals(res[i], bytes[i - 100]);
        }
        for (int i = 100 + minLen; i < 300; i++) {
            assertEquals(res[i], 0);
        }
        res = new byte[300];
        minLen = Math.min(100, len - 50);
        MemoryUtils.readMemory(res, 100, address + 50, minLen);
        for (int i = 0; i < 100; i++) {
            assertEquals(res[i], 0);
        }
        for (int i = 100; i < 100 + minLen; i++) {
            assertEquals(res[i], bytes[i - 50]);
        }
        for (int i = 100 + minLen; i < 300; i++) {
            assertEquals(res[i], 0);
        }
    }

    public void testArrayCopy() {
        byte[] bytes = createRandomByte();
        int len = bytes.length;
        byte[] res = new byte[len];
        MemoryUtils.arraycopy(bytes, len / 2, res, 0, len / 2);
        for (int i = 0; i < len / 2; i++) {
            assertEquals(bytes[len / 2 + i], res[i]);
        }

        for (int i = len / 2; i < len; i++) {
            assertEquals(0, res[i]);
        }

    }


    public void testCopyMemory2() {
        byte[] bytes = createRandomByte();
        int len = bytes.length;
        long address = MemoryUtils.allocate(len * 2);
        MemoryUtils.copyMemory(bytes, address);
        MemoryUtils.copyMemory(bytes, address + len);
        for (int i = 0; i < len; i++) {
            assertEquals(MemoryUtils.getByte(address, len + i), MemoryUtils.getByte(address, i));
        }
    }

    public void testFill0() {
        long address = MemoryUtils.allocate(2000);
        for (int i = 0; i < 2000; i++) {
            MemoryUtils.put(address, i, (byte) 20);
        }
        MemoryUtils.fill0(address + 100, 0);
        MemoryUtils.fill0(address + 50, 50);
        MemoryUtils.fill0(address + 350, 50);
        MemoryUtils.fill0(address + 1350, 50);
        for (int i = 0; i < 50; i++) {
            assertEquals(0, MemoryUtils.getByte(address, i + 50));
        }
        assertEquals(MemoryUtils.getByte(address, 100), 20);
        for (int i = 0; i < 50; i++) {
            assertEquals(20, MemoryUtils.getByte(address, i + 150));
        }
        for (int i = 0; i < 50; i++) {
            assertEquals(0, MemoryUtils.getByte(address, i + 350));
        }

        for (int i = 0; i < 50; i++) {
            assertEquals(0, MemoryUtils.getByte(address, i + 1350));
        }
    }

    public void testReallocate() {
        byte[] bytes = createRandomByte();
        int len = bytes.length;
        long address = MemoryUtils.allocate(len);
        MemoryUtils.copyMemory(bytes, address);
        long a2 = MemoryUtils.reallocate(address, len * 2);
        MemoryUtils.copyMemory(bytes, a2 + len);
        for (int i = 0; i < len; i++) {
            assertEquals(MemoryUtils.getByte(a2, i), MemoryUtils.getByte(a2, len + i));
        }
        MemoryUtils.free(a2);
    }


    public void testDouble() {
        for (int i = 0; i < 10000; i++) {
            doubleTest();
        }
    }

    public void testLong2Double() {
        for (int i = 0; i < 10000; i++) {
            longDoubleTest();
        }
    }

    public void testByte2Double() {
        for (int i = 0; i < 10000; i++) {
            byteDoubleTest();
        }
    }


    public void testOther() {
        for (int i = 0; i < 10000; i++) {
            charTest();
            intTest();
            floatTest();
            longTest();
            shortTest();
            byteTest();
        }
    }

    private double[] createRandomDouble() {
        int len = (int) (Math.random() * 1000);
        double[] arrays = new double[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = Math.random() * 100000000000d;
        }
        return arrays;
    }

    private char[] createRandomChar() {
        int len = (int) (Math.random() * 1000);
        char[] arrays = new char[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (char) Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    private int[] createRandomInt() {
        int len = (int) (Math.random() * 1000);
        int[] arrays = new int[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (int) Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    private void intTest() {
        int[] arrays = createRandomInt();
        int len = arrays.length;
        long s = MemoryUtils.allocate(len << Offset.INT.getOffset());
        for (int i = 0; i < len; i++) {
            MemoryUtils.put(s, i, arrays[i]);
        }
        for (int i = 0; i < len; i++) {
            assertEquals(arrays[i], MemoryUtils.getInt(s, i));
        }
        MemoryUtils.free(s);
    }

    private float[] createRandomFloat() {
        int len = (int) (Math.random() * 1000);
        float[] arrays = new float[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (float) Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    private void floatTest() {
        float[] arrays = createRandomFloat();
        int len = arrays.length;
        long s = MemoryUtils.allocate(len << Offset.FLOAT.getOffset());
        for (int i = 0; i < len; i++) {
            MemoryUtils.put(s, i, arrays[i]);
        }
        for (int i = 0; i < len; i++) {
            assertEquals(arrays[i], MemoryUtils.getFloat(s, i));
        }
        MemoryUtils.free(s);
    }

    private long[] createRandomLong() {
        int len = (int) (Math.random() * 1000);
        long[] arrays = new long[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    private void longTest() {
        long[] arrays = createRandomLong();
        int len = arrays.length;
        long s = MemoryUtils.allocate(len << Offset.LONG.getOffset());
        for (int i = 0; i < len; i++) {
            MemoryUtils.put(s, i, arrays[i]);
        }
        for (int i = 0; i < len; i++) {
            assertEquals(arrays[i], MemoryUtils.getLong(s, i));
        }
        MemoryUtils.free(s);
    }

    private short[] createRandomShort() {
        int len = (int) (Math.random() * 1000);
        short[] arrays = new short[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (short) (Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    private void shortTest() {
        short[] arrays = createRandomShort();
        int len = arrays.length;
        long s = MemoryUtils.allocate(len << Offset.SHORT.getOffset());
        for (int i = 0; i < len; i++) {
            MemoryUtils.put(s, i, arrays[i]);
        }
        for (int i = 0; i < len; i++) {
            assertEquals(arrays[i], MemoryUtils.getShort(s, i));
        }
        MemoryUtils.free(s);
    }

    private byte[] createRandomByte() {
        int len = (int) (Math.random() * 1000);
        byte[] arrays = new byte[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (byte) (Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    private void byteTest() {
        byte[] arrays = createRandomByte();
        int len = arrays.length;
        long s = MemoryUtils.allocate(len);
        for (int i = 0; i < len; i++) {
            MemoryUtils.put(s, i, arrays[i]);
        }
        for (int i = 0; i < len; i++) {
            assertEquals(arrays[i], MemoryUtils.getByte(s, i));
        }
        MemoryUtils.free(s);
    }

    private void charTest() {
        char[] arrays = createRandomChar();
        int len = arrays.length;
        long s = MemoryUtils.allocate(len << Offset.CHAR.getOffset());
        for (int i = 0; i < len; i++) {
            MemoryUtils.put(s, i, arrays[i]);
        }
        for (int i = 0; i < len; i++) {
            assertEquals(arrays[i], MemoryUtils.getChar(s, i));
        }
        MemoryUtils.free(s);
    }

    private void doubleTest() {
        double[] arrays = createRandomDouble();
        int len = arrays.length;
        long s = MemoryUtils.allocate(len << Offset.DOUBLE.getOffset());
        for (int i = 0; i < len; i++) {
            MemoryUtils.put(s, i, arrays[i]);
        }
        for (int i = 0; i < len; i++) {
            assertEquals(arrays[i], MemoryUtils.getDouble(s, i));
        }
        MemoryUtils.free(s);
    }

    private void longDoubleTest() {
        double[] arrays = createRandomDouble();
        int len = arrays.length;
        long s = MemoryUtils.allocate(len << Offset.DOUBLE.getOffset());
        for (int i = 0; i < len; i++) {
            MemoryUtils.put(s, i, Double.doubleToLongBits(arrays[i]));
        }
        for (int i = 0; i < len; i++) {
            assertEquals(arrays[i], MemoryUtils.getDouble(s, i));
        }
        MemoryUtils.free(s);
    }

    private void byteDoubleTest() {
        double[] arrays = createRandomDouble();
        int len = arrays.length;
        long s = MemoryUtils.allocate(len << Offset.DOUBLE.getOffset());
        for (int i = 0; i < len; i++) {
            byte[] bytes = long2byte(Double.doubleToLongBits(arrays[i]));
            for (int j = 0; j < 8; j++) {
                MemoryUtils.put(s, (i << Offset.DOUBLE.getOffset()) + j, bytes[j]);
            }
        }
        for (int i = 0; i < len; i++) {
            assertEquals(arrays[i], MemoryUtils.getDouble(s, i));
        }
        MemoryUtils.free(s);

    }

    private byte[] long2byte(long v) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(v).array();
    }
}

