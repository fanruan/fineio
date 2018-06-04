package com.fineio.test.io.write;

import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.test.file.FineWriteIOTest;
import com.fineio.v1.FineIO;
import com.fineio.v1.io.base.AbstractBuffer;
import com.fineio.v1.io.base.BaseBuffer;
import com.fineio.v1.io.file.IOFile;
import com.fineio.v1.io.write.ByteWriteBuffer;
import com.fineio.v1.io.write.CharWriteBuffer;
import com.fineio.v1.io.write.DoubleWriteBuffer;
import com.fineio.v1.io.write.FloatWriteBuffer;
import com.fineio.v1.io.write.IntWriteBuffer;
import com.fineio.v1.io.write.LongWriteBuffer;
import com.fineio.v1.io.write.ShortWriteBuffer;
import com.fineio.v1.io.write.WriteBuffer;
import junit.framework.TestCase;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * Created by daniel on 2017/2/20.
 */
public class WriteBufferTest extends TestCase {


    private static <T extends WriteBuffer> T getWriteBuffer(IOFile<?> writeIOFile, Class<T> clazz) {
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


    private byte[] createRandomByte(int off){
        int len = 1 << off;
        byte[] arrays = new byte[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (byte)(Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    private char[] createRandomChar(int off){
        int len = 1 << off;
        char[] arrays = new char[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (char)(Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    private int[] createRandomInt(int off){
        int len = 1 << off;
        int[] arrays = new int[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (int)(Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    private long[] createRandomLong(int off){
        int len = 1 << off;
        long[] arrays = new long[len];
        for(int i = 0; i< len; i++){
            arrays[i] = Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    private short[] createRandomShort(int off){
        int len = 1 << off;
        short[] arrays = new short[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (short)(Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }


    private float[] createRandomFloat(int off){
        int len = 1 << off;
        float[] arrays = new float[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (float)(Math.random() * 100000000000d);
        }
        return arrays;
    }

    private double[] createRandomDouble(int off){
        int len = 1 << off;
        double[] arrays = new double[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (Math.random() * 100000000000d);
        }
        return arrays;
    }

    public void testByteWrite() throws  Exception {
        int len = 20;
        byte[] bytes = createRandomByte(len);
        Constructor<ByteWriteBuffer> constructor = ByteWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        ByteWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }

        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getByte(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (byte) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);

        ByteWriteBuffer bb2 = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);

        for(int i = 0;i < bytes.length; i++){
            bb2.put(bytes[i]);
        }

        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], bb2.get(i));
        }

        byte[] bytesRes1 = new byte[bytes.length];
        byte[] bytesRes2 = new byte[bytes.length];
        InputStream is = getInputStream(bb);
        assertEquals(bytes.length, is.read(bytesRes1));
        InputStream is2 = getInputStream(bb2);
        assertEquals(bytes.length, is2.read(bytesRes2));

        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], bytesRes1[i]);
            assertEquals(bytes[i], bytesRes2[i]);
        }
        bb.force();
        bb2.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    private InputStream getInputStream(BaseBuffer sb) {
        try {
            Method m =  AbstractBuffer.class.getDeclaredMethod("getInputStream");
            m.setAccessible(true);
            return (InputStream) m.invoke(sb);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  null;
    }


    public void testCharWrite() throws  Exception {
        int len = 20;
        char[] bytes = createRandomChar(len);
        Constructor<CharWriteBuffer> constructor = CharWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        CharWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getChar(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (char) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);



        CharWriteBuffer bb2 = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);

        for(int i = 0;i < bytes.length; i++){
            bb2.put(bytes[i]);
        }

        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], bb2.get(i));
        }

        byte[] bytesRes1 = new byte[bytes.length << 1];
        byte[] bytesRes2 = new byte[bytes.length << 1];
        InputStream is = getInputStream(bb);
        assertEquals(bytes.length << 1, is.read(bytesRes1));
        InputStream is2 = getInputStream(bb2);
        assertEquals(bytes.length << 1, is2.read(bytesRes2));

        for(int i = 0;i < (bytes.length << 1); i++){
            assertEquals(bytesRes1[i], bytesRes2[i]);
        }
        bb.force();
        bb2.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testCharWriteDESC() throws  Exception {
        int len = 20;
        char[] bytes = createRandomChar(len);
        Constructor<CharWriteBuffer> constructor = CharWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        CharWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i = bytes.length;i > 0 ; i--){
            bb.put(i - 1, bytes[i -1]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getChar(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (char) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);

        CharWriteBuffer bb2 = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);

        for(int i = 0;i < bytes.length; i++){
            bb2.put(bytes[i]);
        }

        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], bb2.get(i));
        }

        byte[] bytesRes1 = new byte[bytes.length << 1];
        byte[] bytesRes2 = new byte[bytes.length << 1];
        InputStream is = getInputStream(bb);
        assertEquals(bytes.length << 1, is.read(bytesRes1));
        InputStream is2 = getInputStream(bb2);
        assertEquals(bytes.length << 1, is2.read(bytesRes2));

        for(int i = 0;i < (bytes.length << 1); i++){
            assertEquals(bytesRes1[i], bytesRes2[i]);
        }
        bb.force();
        bb2.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);


    }

    public void testDoubleWriteDESC() throws  Exception {
        int len = 20;
        double[] bytes = createRandomDouble(len);
        Constructor<DoubleWriteBuffer> constructor = DoubleWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        DoubleWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i =  bytes.length;i > 0; i--){
            bb.put(i - 1, bytes[i -1]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getDouble(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (double) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        DoubleWriteBuffer bb2 = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);

        for(int i = 0;i < bytes.length; i++){
            bb2.put(bytes[i]);
        }

        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], bb2.get(i));
        }

        byte[] bytesRes1 = new byte[bytes.length << 3];
        byte[] bytesRes2 = new byte[bytes.length << 3];
        InputStream is = getInputStream(bb);
        assertEquals(bytes.length << 3, is.read(bytesRes1));
        InputStream is2 = getInputStream(bb2);
        assertEquals(bytes.length << 3, is2.read(bytesRes2));

        for(int i = 0;i < (bytes.length << 3); i++){
            assertEquals(bytesRes1[i], bytesRes2[i]);
        }
        bb.force();
        bb2.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testDoubleWrite() throws  Exception {
        int len = 20;
        double[] bytes = createRandomDouble(len);
        Constructor<DoubleWriteBuffer> constructor = DoubleWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        DoubleWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getDouble(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (double) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        DoubleWriteBuffer bb2 = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);

        for(int i = 0;i < bytes.length; i++){
            bb2.put(bytes[i]);
        }

        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], bb2.get(i));
        }

        byte[] bytesRes1 = new byte[bytes.length << 3];
        byte[] bytesRes2 = new byte[bytes.length << 3];
        InputStream is = getInputStream(bb);
        assertEquals(bytes.length << 3, is.read(bytesRes1));
        InputStream is2 = getInputStream(bb2);
        assertEquals(bytes.length << 3, is2.read(bytesRes2));

        for(int i = 0;i < (bytes.length << 3); i++){
            assertEquals(bytesRes1[i], bytesRes2[i]);
        }
        bb.force();
        bb2.force();
        exp = false;
        try {
            bb.put(0, 1);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        try {
            bb.get(0);
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


    public void testFloatWrite() throws  Exception {
        int len = 20;
        float[] bytes = createRandomFloat(len);
        Constructor<FloatWriteBuffer> constructor = FloatWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        FloatWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getFloat(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (float) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testFloatWriteDESC() throws  Exception {
        int len = 20;
        float[] bytes = createRandomFloat(len);
        Constructor<FloatWriteBuffer> constructor = FloatWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        FloatWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i = bytes.length;i > 0 ; i--){
            bb.put(i - 1, bytes[i -1]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getFloat(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (float) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testIntWrite() throws  Exception {
        int len = 20;
        int[] bytes = createRandomInt(len);
        Constructor<IntWriteBuffer> constructor = IntWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        IntWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getInt(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testIntWriteDESC() throws  Exception {
        int len = 20;
        int[] bytes = createRandomInt(len);
        Constructor<IntWriteBuffer> constructor = IntWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        IntWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i = bytes.length;i > 0 ; i--){
            bb.put(i - 1, bytes[i -1]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getInt(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    public void testLongWrite() throws  Exception {
        int len = 20;
        long[] bytes = createRandomLong(len);
        Constructor<LongWriteBuffer> constructor = LongWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        LongWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getLong(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (long) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testLongWriteDESC() throws  Exception {
        int len = 20;
        long[] bytes = createRandomLong(len);
        Constructor<LongWriteBuffer> constructor = LongWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        LongWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i =  bytes.length;i > 0; i--){
            bb.put(i - 1, bytes[i - 1]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getLong(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (long) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testShortWrite() throws  Exception {
        int len = 20;
        short[] bytes = createRandomShort(len);
        Constructor<ShortWriteBuffer> constructor = ShortWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        ShortWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getShort(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (short) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testShortWriteDESC() throws  Exception {
        int len = 20;
        short[] bytes = createRandomShort(len);
        Constructor<ShortWriteBuffer> constructor = ShortWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        ShortWriteBuffer bb = constructor.newInstance(new FineWriteIOTest.MemoryConnector(), new FileBlock(new URI(""), ""), len);
        for(int i =bytes.length;i > 0 ; i--){
            bb.put(i -1, bytes[i -1]);
        }
        Field field = AbstractBuffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getShort(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (short) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
        bb.force();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }
}
