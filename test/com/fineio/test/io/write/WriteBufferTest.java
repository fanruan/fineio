package com.fineio.test.io.write;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.file.FileConstants;
import com.fineio.file.IOFile;
import com.fineio.io.base.AbstractBuffer;
import com.fineio.io.write.*;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;

import java.io.ByteArrayInputStream;
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


    public void  testOffSet() throws  Exception {
        int len = (int)(Math.random() * 100d);
        final byte[] res = new byte[16];
        Bits.putLong(res, 0, (long)len);
        Bits.putLong(res, 8, (long)len * 2);
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Field head = FileConstants.class.getDeclaredField("HEAD");
        head.setAccessible(true);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, head.get(null));
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            @Override
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(res);
            }
        }).anyTimes();
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22).anyTimes();
        control.replay();
        IOFile writeIOFile = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_LONG);
        ByteWriteBuffer byteWriteBuffer = getWriteBuffer(writeIOFile,ByteWriteBuffer.class );
        Method method = ByteWriteBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        int v = (Integer) method.invoke(byteWriteBuffer);
        assertEquals(v, MemoryConstants.OFFSET_BYTE);
        DoubleWriteBuffer doubleWriteBuffer = getWriteBuffer(writeIOFile, DoubleWriteBuffer.class);
        method = DoubleWriteBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(doubleWriteBuffer);
        LongWriteBuffer longWriteBuffer = getWriteBuffer(writeIOFile, LongWriteBuffer.class );
        method = LongWriteBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(longWriteBuffer);
        assertEquals(v, MemoryConstants.OFFSET_LONG);
        IntWriteBuffer intWriteBuffer = getWriteBuffer(writeIOFile, IntWriteBuffer.class );
        method = IntWriteBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(intWriteBuffer);
        assertEquals(v, MemoryConstants.OFFSET_INT);
        CharWriteBuffer charWriteBuffer = getWriteBuffer(writeIOFile, CharWriteBuffer.class );
        method = CharWriteBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(charWriteBuffer);
        assertEquals(v, MemoryConstants.OFFSET_CHAR);
        FloatWriteBuffer floatWriteBuffer = getWriteBuffer(writeIOFile, FloatWriteBuffer.class );
        method = FloatWriteBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(floatWriteBuffer);
        assertEquals(v, MemoryConstants.OFFSET_FLOAT);

        ShortWriteBuffer shortWriteBuffer = getWriteBuffer(writeIOFile, ShortWriteBuffer.class );
        method = ShortWriteBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(shortWriteBuffer);
        assertEquals(v, MemoryConstants.OFFSET_SHORT);

    }

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
    }

    public void testCharWriteDESC() throws  Exception {
        int len = 20;
        char[] bytes = createRandomChar(len);
        Constructor<CharWriteBuffer> constructor = CharWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        CharWriteBuffer bb = constructor.newInstance(null, null, len);
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
    }

    public void testDoubleWriteDESC() throws  Exception {
        int len = 20;
        double[] bytes = createRandomDouble(len);
        Constructor<DoubleWriteBuffer> constructor = DoubleWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        DoubleWriteBuffer bb = constructor.newInstance(null, null, len);
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
    }

    public void testFloatWriteDESC() throws  Exception {
        int len = 20;
        float[] bytes = createRandomFloat(len);
        Constructor<FloatWriteBuffer> constructor = FloatWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        FloatWriteBuffer bb = constructor.newInstance(null, null, len);
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
        Field field = AbstractBuffer.class.getDeclaredField("address");
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

    public void testIntWriteDESC() throws  Exception {
        int len = 20;
        int[] bytes = createRandomInt(len);
        Constructor<IntWriteBuffer> constructor = IntWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        IntWriteBuffer bb = constructor.newInstance(null, null, len);
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
    }

    public void testLongWriteDESC() throws  Exception {
        int len = 20;
        long[] bytes = createRandomLong(len);
        Constructor<LongWriteBuffer> constructor = LongWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        LongWriteBuffer bb = constructor.newInstance(null, null, len);
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
    }

    public void testShortWriteDESC() throws  Exception {
        int len = 20;
        short[] bytes = createRandomShort(len);
        Constructor<ShortWriteBuffer> constructor = ShortWriteBuffer.class.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
        constructor.setAccessible(true);
        ShortWriteBuffer bb = constructor.newInstance(null, null, len);
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
    }
}
