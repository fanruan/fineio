package com.fineio.test.io.read;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.file.FileConstants;
import com.fineio.file.IOFile;
import com.fineio.io.base.AbstractBuffer;
import com.fineio.io.read.*;
import com.fineio.memory.MemoryConstants;
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
 * Created by daniel on 2017/2/13.
 */
public class BufferTest  extends TestCase {


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
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(res);
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            @Override
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(res);
            }
        }).anyTimes();
        control.replay();
        IOFile readIOFile = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_LONG);
        ByteReadBuffer byteReadBuffer = getReadBuffer(readIOFile,ByteReadBuffer.class );
        Method method = ByteReadBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        int v = (Integer) method.invoke(byteReadBuffer);
        assertEquals(v, MemoryConstants.OFFSET_BYTE);
        DoubleReadBuffer doubleReadBuffer = getReadBuffer(readIOFile, DoubleReadBuffer.class);
        method = DoubleReadBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(doubleReadBuffer);
        LongReadBuffer longReadBuffer = getReadBuffer(readIOFile, LongReadBuffer.class );
        method = LongReadBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(longReadBuffer);
        assertEquals(v, MemoryConstants.OFFSET_LONG);
        IntReadBuffer intReadBuffer = getReadBuffer(readIOFile, IntReadBuffer.class );
        method = IntReadBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(intReadBuffer);
        assertEquals(v, MemoryConstants.OFFSET_INT);
        CharReadBuffer charReadBuffer = getReadBuffer(readIOFile, CharReadBuffer.class );
        method = CharReadBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(charReadBuffer);
        assertEquals(v, MemoryConstants.OFFSET_CHAR);
        FloatReadBuffer floatReadBuffer = getReadBuffer(readIOFile, FloatReadBuffer.class );
        method = FloatReadBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(floatReadBuffer);
        assertEquals(v, MemoryConstants.OFFSET_FLOAT);

        ShortReadBuffer shortReadBuffer = getReadBuffer(readIOFile, ShortReadBuffer.class );
        method = ShortReadBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(shortReadBuffer);
        assertEquals(v, MemoryConstants.OFFSET_SHORT);

    }

    private static <T extends ReadBuffer> T getReadBuffer(IOFile<?> readIOFile, Class<T> clazz) {
        try {
            Method method = IOFile.class.getDeclaredMethod("createBuffer", Class.class, int.class);
            method.setAccessible(true);
            return (T) method.invoke(readIOFile, clazz, 0);
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

    public void testBuffer100() throws Exception {
        for(int i = 0; i< 100; i++) {
            testBuffer();
        }
    }

    public void testBuffer() throws Exception {
        int len = 10;
        final byte[] value = createRandomByte(len);
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, "0");
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            @Override
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(value);
            }
        }).anyTimes();
        control.replay();
        byteTest(value, connector, block, len);
        intTest(value, connector, block, len);
        doubleTest(value, connector, block, len);
        charTest(value, connector, block, len);
        shortTest(value, connector, block, len);
        longTest(value, connector, block, len);
        floatTest(value, connector, block, len);
    }

    private  static  <T extends AbstractBuffer>  T createBuffer(Class<T> clazz, Object connector, Object block, int offset) {
        try {
            Constructor<T>  constructor= clazz.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
            constructor.setAccessible(true);
            return  constructor.newInstance(connector, block, offset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void byteTest(byte[] value, Connector connector, FileBlock block, int off) {
        ByteReadBuffer buffer = createBuffer(ByteReadBuffer.class, connector, block, off);
        byte r = 0;
        for(int k = 0; k < value.length; k++){
            r +=value[k];
        }
        byte r2 = 0;
        for(int k = 0; k < value.length; k++){
            r2 +=buffer.get(k);
        }
        assertEquals(r, r2);
        boolean exp = false;
        try {
            buffer.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        buffer.clear();
    }

    private void intTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;IntReadBuffer ib = createBuffer(IntReadBuffer.class, connector, block, off -2);
        int v1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=4){
            v1 += Bits.getInt(value, k);
        }
        int v2 = 0;
        for(int k = 0, klen = (value.length >> 2); k < klen; k++){
            v2 +=ib.get(k);
        }
        assertEquals(v1, v2);
        exp = false;
        try {
            ib.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        ib.clear();
    }


    private void floatTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;FloatReadBuffer ib = createBuffer(FloatReadBuffer.class, connector, block, off -2);
        float v1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=4){
            v1 += Bits.getFloat(value, k);
        }
        float v2 = 0;
        for(int k = 0, klen = (value.length >> 2); k < klen; k++){
            v2 +=ib.get(k);
        }
        assertEquals(v1, v2);
        exp = false;
        try {
            ib.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        ib.clear();
    }

    private void doubleTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;DoubleReadBuffer db = createBuffer(DoubleReadBuffer.class, connector, block, off -3);
        double d1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=8){
            d1 += Bits.getDouble(value, k);
        }
        double d2 = 0;
        for(int k = 0, klen = (value.length >> 3); k < klen; k++){
            d2 +=db.get(k);
        }
        assertEquals(d1, d2);
        exp = false;
        try {
            db.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        db.clear();
    }

    private void charTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;CharReadBuffer cb = createBuffer(CharReadBuffer.class, connector, block, off -1);
        char c1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=2){
            c1 += Bits.getChar(value, k);
        }
        char c2 = 0;
        for(int k = 0, klen = (value.length >> 1); k < klen; k++){
            c2 +=cb.get(k);
        }
        assertEquals(c1, c2);
        exp = false;
        try {
            cb.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        cb.clear();
    }


    private void shortTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;ShortReadBuffer cb = createBuffer(ShortReadBuffer.class,connector, block, off -1);
        short c1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=2){
            c1 += Bits.getChar(value, k);
        }
        short c2 = 0;
        for(int k = 0, klen = (value.length >> 1); k < klen; k++){
            c2 +=cb.get(k);
        }
        assertEquals(c1, c2);
        exp = false;
        try {
            cb.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        cb.clear();
    }


    private void longTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;LongReadBuffer db = createBuffer(LongReadBuffer.class,connector, block, off - 3);
        long d1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=8){
            d1 += Bits.getLong(value, k);
        }
        long d2 = 0;
        for(int k = 0, klen = (value.length >> 3); k < klen; k++){
            d2 +=db.get(k);
        }
        assertEquals(d1, d2);
        exp = false;
        try {
            db.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        db.clear();
    }

    public void testThread() throws Exception{
        int i = 0;
        while (i ++ < 10) {
            testMultiThread();
        }
    }

    public void testMultiThread() throws Exception{
        final byte[] value = createRandomByte(10);
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, "0");
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            @Override
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(value);
            }
        }).anyTimes();
        control.replay();
        final ByteReadBuffer buffer =  createBuffer(ByteReadBuffer.class, connector, block, 10);
        Thread[] t = new Thread[1000];
        for(int i = 0; i < t.length; i++){
            if((i & 1) == 0) {
                t[i] = new Thread() {
                    public void run() {
                            byte b = 0;
                            for (int k = 0; k < value.length; k++) {
                                b += buffer.get(k);
                            }
                    }
                };
            } else {
                t[i] = new Thread() {
                    public void run() {
                        for(int k = 0; k < value.length; k++){
                            buffer.clear();
                        }
                    }
                };
            }
        }
        for(int i = 0; i < t.length; i++){
            t[i].start();
        }
        for(int i = 0; i < t.length; i++){
            t[i].join();
        }

    }

}
