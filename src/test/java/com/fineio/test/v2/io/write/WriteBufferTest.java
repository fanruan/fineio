package com.fineio.test.v2.io.write;

import com.fineio.FineIO;
import com.fineio.cache.CacheManager;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.test.file.FineWriteIOTest;
import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.net.URI;

/**
 * Created by daniel on 2017/2/20.
 */
public class WriteBufferTest extends TestCase {

    private byte[] createRandomByte(int off) {
        int len = 1 << off;
        byte[] arrays = new byte[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (byte) (Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    private char[] createRandomChar(int off) {
        int len = 1 << off;
        char[] arrays = new char[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (char) (Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    private int[] createRandomInt(int off) {
        int len = 1 << off;
        int[] arrays = new int[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (int) (Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    private long[] createRandomLong(int off) {
        int len = 1 << off;
        long[] arrays = new long[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    private short[] createRandomShort(int off) {
        int len = 1 << off;
        short[] arrays = new short[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (short) (Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }


    private float[] createRandomFloat(int off) {
        int len = 1 << off;
        float[] arrays = new float[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (float) (Math.random() * 100000000000d);
        }
        return arrays;
    }

    private double[] createRandomDouble(int off) {
        int len = 1 << off;
        double[] arrays = new double[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (Math.random() * 100000000000d);
        }
        return arrays;
    }

    public void testByteWrite() throws Exception {
        int len = 20;
        byte[] bytes = createRandomByte(len);
        ByteBuffer.ByteWriteBuffer bb = CacheManager.DataType.BYTE.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = 0; i < bytes.length; i++) {
            bb.put(i, bytes[i]);
        }

        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getByte(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (byte) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testCharWrite() throws Exception {
        int len = 20;
        char[] bytes = createRandomChar(len);
        CharBuffer.CharWriteBuffer bb = CacheManager.DataType.CHAR.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = 0; i < bytes.length; i++) {
            bb.put(i, bytes[i]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getChar(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (char) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testCharWriteDESC() throws Exception {
        int len = 20;
        char[] bytes = createRandomChar(len);
        CharBuffer.CharWriteBuffer bb = CacheManager.DataType.CHAR.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = bytes.length; i > 0; i--) {
            bb.put(i - 1, bytes[i - 1]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getChar(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (char) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);


    }

    public void testDoubleWriteDESC() throws Exception {
        int len = 20;
        double[] bytes = createRandomDouble(len);
        DoubleBuffer.DoubleWriteBuffer bb = CacheManager.DataType.DOUBLE.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = bytes.length; i > 0; i--) {
            bb.put(i - 1, bytes[i - 1]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getDouble(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (double) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);

        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testDoubleWrite() throws Exception {
        int len = 20;
        double[] bytes = createRandomDouble(len);
        DoubleBuffer.DoubleWriteBuffer bb = CacheManager.DataType.DOUBLE.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = 0; i < bytes.length; i++) {
            bb.put(i, bytes[i]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getDouble(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (double) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        exp = false;
        try {
            bb.put(0, 1);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testFloatWrite() throws Exception {
        int len = 20;
        float[] bytes = createRandomFloat(len);
        FloatBuffer.FloatWriteBuffer bb = CacheManager.DataType.FLOAT.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = 0; i < bytes.length; i++) {
            bb.put(i, bytes[i]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getFloat(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (float) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testFloatWriteDESC() throws Exception {
        int len = 20;
        float[] bytes = createRandomFloat(len);
        FloatBuffer.FloatWriteBuffer bb = CacheManager.DataType.FLOAT.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = bytes.length; i > 0; i--) {
            bb.put(i - 1, bytes[i - 1]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getFloat(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (float) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testIntWrite() throws Exception {
        int len = 20;
        int[] bytes = createRandomInt(len);
        IntBuffer.IntWriteBuffer bb = CacheManager.DataType.INT.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = 0; i < bytes.length; i++) {
            bb.put(i, bytes[i]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getInt(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testIntWriteDESC() throws Exception {
        int len = 20;
        int[] bytes = createRandomInt(len);
        IntBuffer.IntWriteBuffer bb = CacheManager.DataType.INT.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = bytes.length; i > 0; i--) {
            bb.put(i - 1, bytes[i - 1]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getInt(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testLongWrite() throws Exception {
        int len = 20;
        long[] bytes = createRandomLong(len);
        LongBuffer.LongWriteBuffer bb = CacheManager.DataType.LONG.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = 0; i < bytes.length; i++) {
            bb.put(i, bytes[i]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getLong(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (long) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testLongWriteDESC() throws Exception {
        int len = 20;
        long[] bytes = createRandomLong(len);
        LongBuffer.LongWriteBuffer bb = CacheManager.DataType.LONG.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = bytes.length; i > 0; i--) {
            bb.put(i - 1, bytes[i - 1]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getLong(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (long) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testShortWrite() throws Exception {
        int len = 20;
        short[] bytes = createRandomShort(len);
        ShortBuffer.ShortWriteBuffer bb = CacheManager.DataType.SHORT.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = 0; i < bytes.length; i++) {
            bb.put(i, bytes[i]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getShort(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (short) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testShortWriteDESC() throws Exception {
        int len = 20;
        short[] bytes = createRandomShort(len);
        ShortBuffer.ShortWriteBuffer bb = CacheManager.DataType.SHORT.createBuffer(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).asWrite();
        for (int i = bytes.length; i > 0; i--) {
            bb.put(i - 1, bytes[i - 1]);
        }
        Method field = Buffer.class.getDeclaredMethod("getAddress");
        field.setAccessible(true);
        long address = (Long) field.invoke(bb);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getShort(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (short) 0);
        } catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }
}
