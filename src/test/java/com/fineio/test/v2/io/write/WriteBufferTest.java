package com.fineio.test.v2.io.write;

import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.test.file.FineWriteIOTest;
import com.fineio.v2.FineIO;
import com.fineio.v2.io.BaseBuffer;
import com.fineio.v2.io.Buffer;
import com.fineio.v2.io.ByteBuffer;
import com.fineio.v2.io.CharBuffer;
import com.fineio.v2.io.DoubleBuffer;
import com.fineio.v2.io.FloatBuffer;
import com.fineio.v2.io.IntBuffer;
import com.fineio.v2.io.LongBuffer;
import com.fineio.v2.io.ShortBuffer;
import com.fineio.v2.io.edit.ByteEditBuffer;
import com.fineio.v2.io.file.IOFile;
import com.fineio.v2.io.write.ByteWriteBuffer;
import com.fineio.v2.io.write.CharWriteBuffer;
import com.fineio.v2.io.write.DoubleWriteBuffer;
import com.fineio.v2.io.write.FloatWriteBuffer;
import com.fineio.v2.io.write.IntWriteBuffer;
import com.fineio.v2.io.write.LongWriteBuffer;
import com.fineio.v2.io.write.ShortWriteBuffer;
import com.fineio.v2.io.write.WriteOnlyBuffer;
import junit.framework.TestCase;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * Created by daniel on 2017/2/20.
 */
public class WriteBufferTest extends TestCase {


    private static <T extends WriteOnlyBuffer> T getWriteBuffer(IOFile<?> writeIOFile, Class<T> clazz) {
        try {
            Method method = IOFile.class.getDeclaredMethod("createBuffer", Class.class, int.class);
            method.setAccessible(true);
            return (T) method.invoke(writeIOFile, clazz, 0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }


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
        Constructor<ByteBuffer> constructor = ByteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        ByteWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        Method flip = bb.getClass().getSuperclass().getDeclaredMethod("flip");
        flip.setAccessible(true);
        flip.invoke(bb);
        ByteEditBuffer bb2 = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).editBuffer();

        for (int i = 0; i < bytes.length; i++) {
            bb2.put(bytes[i]);
        }

        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], bb2.get(i));
        }

        byte[] bytesRes1 = new byte[bytes.length];
        byte[] bytesRes2 = new byte[bytes.length];
        InputStream is = getInputStream((BaseBuffer) bb);
        assertEquals(bytes.length, is.read(bytesRes1));
        InputStream is2 = getInputStream((BaseBuffer) bb2);
        assertEquals(bytes.length, is2.read(bytesRes2));

        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], bytesRes1[i]);
            assertEquals(bytes[i], bytesRes2[i]);
        }
        bb.forceAndClear();
        bb2.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    private InputStream getInputStream(BaseBuffer sb) {
        try {
            Method m = BaseBuffer.class.getDeclaredMethod("getInputStream");
            m.setAccessible(true);
            return (InputStream) m.invoke(sb);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void testCharWrite() throws Exception {
        int len = 20;
        char[] bytes = createRandomChar(len);
        Constructor<CharBuffer> constructor = CharBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        CharWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testCharWriteDESC() throws Exception {
        int len = 20;
        char[] bytes = createRandomChar(len);
        Constructor<CharBuffer> constructor = CharBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        CharWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);


    }

    public void testDoubleWriteDESC() throws Exception {
        int len = 20;
        double[] bytes = createRandomDouble(len);
        Constructor<DoubleBuffer> constructor = DoubleBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        DoubleWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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

        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testDoubleWrite() throws Exception {
        int len = 20;
        double[] bytes = createRandomDouble(len);
        Constructor<DoubleBuffer> constructor = DoubleBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        DoubleWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
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
        Constructor<FloatBuffer> constructor = FloatBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        FloatWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testFloatWriteDESC() throws Exception {
        int len = 20;
        float[] bytes = createRandomFloat(len);
        Constructor<FloatBuffer> constructor = FloatBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        FloatWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testIntWrite() throws Exception {
        int len = 20;
        int[] bytes = createRandomInt(len);
        Constructor<IntBuffer> constructor = IntBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        IntWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testIntWriteDESC() throws Exception {
        int len = 20;
        int[] bytes = createRandomInt(len);
        Constructor<IntBuffer> constructor = IntBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        IntWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testLongWrite() throws Exception {
        int len = 20;
        long[] bytes = createRandomLong(len);
        Constructor<LongBuffer> constructor = LongBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        LongWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testLongWriteDESC() throws Exception {
        int len = 20;
        long[] bytes = createRandomLong(len);
        Constructor<LongBuffer> constructor = LongBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        LongWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testShortWrite() throws Exception {
        int len = 20;
        short[] bytes = createRandomShort(len);
        Constructor<ShortBuffer> constructor = ShortBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        ShortWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testShortWriteDESC() throws Exception {
        int len = 20;
        short[] bytes = createRandomShort(len);
        Constructor<ShortBuffer> constructor = ShortBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        ShortWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len).writeOnlyBuffer();
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
        bb.forceAndClear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }
}
