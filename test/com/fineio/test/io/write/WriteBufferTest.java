package com.fineio.test.io.write;

import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.io.Buffer;
import com.fineio.io.write.*;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fr.third.org.apache.poi.hssf.record.formula.functions.T;
import junit.framework.TestCase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Created by daniel on 2017/2/20.
 */
public class WriteBufferTest extends TestCase {

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
            arrays[i] =  (long)(Double.doubleToLongBits(Math.random() * 100000000000d));
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
        ByteWriteBuffer bb = constructor.newInstance(null, null, len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = Buffer.class.getDeclaredField("address");
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
    }


    public void testCharWrite() throws  Exception {
        int len = 20;
        char[] bytes = createRandomChar(len);
        Constructor<CharWriteBuffer> constructor = CharWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        CharWriteBuffer bb = constructor.newInstance(null, null, len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = Buffer.class.getDeclaredField("address");
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
    }

    public void testDoubleWrite() throws  Exception {
        int len = 20;
        double[] bytes = createRandomDouble(len);
        Constructor<DoubleWriteBuffer> constructor = DoubleWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        DoubleWriteBuffer bb = constructor.newInstance(null, null, len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = Buffer.class.getDeclaredField("address");
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
    }


    public void testFloatWrite() throws  Exception {
        int len = 20;
        float[] bytes = createRandomFloat(len);
        Constructor<FloatWriteBuffer> constructor = FloatWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        FloatWriteBuffer bb = constructor.newInstance(null, null, len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = Buffer.class.getDeclaredField("address");
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
    }


    public void testIntWrite() throws  Exception {
        int len = 20;
        int[] bytes = createRandomInt(len);
        Constructor<IntWriteBuffer> constructor = IntWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        IntWriteBuffer bb = constructor.newInstance(null, null, len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = Buffer.class.getDeclaredField("address");
        field.setAccessible(true);
        long address = (Long)field.get(bb);
        for(int i = 0;i < bytes.length; i++){
            assertEquals(bytes[i], MemoryUtils.getInt(address, i));
        }
        boolean exp = false;
        try {
            bb.put(bytes.length, (int) 0);
        }catch (BufferIndexOutOfBoundsException exception) {
            exp = true;
        }
        assertTrue(exp);
    }

    public void testLongWrite() throws  Exception {
        int len = 20;
        long[] bytes = createRandomLong(len);
        Constructor<LongWriteBuffer> constructor = LongWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        LongWriteBuffer bb = constructor.newInstance(null, null, len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = Buffer.class.getDeclaredField("address");
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
    }

    public void testShortWrite() throws  Exception {
        int len = 20;
        short[] bytes = createRandomShort(len);
        Constructor<ShortWriteBuffer> constructor = ShortWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        ShortWriteBuffer bb = constructor.newInstance(null, null, len);
        for(int i = 0;i < bytes.length; i++){
            bb.put(i, bytes[i]);
        }
        Field field = Buffer.class.getDeclaredField("address");
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
    }
}
