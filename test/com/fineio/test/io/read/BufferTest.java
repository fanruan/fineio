package com.fineio.test.io.read;

import com.fineio.base.Bits;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.io.Buffer;
import com.fineio.io.read.*;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/**
 * Created by daniel on 2017/2/13.
 */
public class BufferTest  extends TestCase {


    public void  testOffSet() {

    }


    private byte[] createRandomByte(){
        int len = 1 << 10;
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

        byte[] value = createRandomByte();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, "0");
        EasyMock.expect(connector.read(EasyMock.eq(block))).andReturn(value).anyTimes();
        control.replay();
        byteTest(value, connector, block);
        intTest(value, connector, block);
        doubleTest(value, connector, block);
        charTest(value, connector, block);
        shortTest(value, connector, block);
        longTest(value, connector, block);
        floatTest(value, connector, block);
    }

    private  static  <T extends Buffer>  T createBuffer(Class<T> clazz, Object connector, Object block) {
        try {
            Constructor<T>  constructor= clazz.getDeclaredConstructor(Connector.class, FileBlock.class);
            constructor.setAccessible(true);
            return  constructor.newInstance(connector, block);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void byteTest(byte[] value, Connector connector, FileBlock block) {
        ByteReadBuffer buffer = createBuffer(ByteReadBuffer.class, connector, block);
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

    private void intTest(byte[] value, Connector connector, FileBlock block) {
        boolean exp;IntReadBuffer ib = createBuffer(IntReadBuffer.class, connector, block);
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


    private void floatTest(byte[] value, Connector connector, FileBlock block) {
        boolean exp;FloatReadBuffer ib = createBuffer(FloatReadBuffer.class, connector, block);
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

    private void doubleTest(byte[] value, Connector connector, FileBlock block) {
        boolean exp;DoubleReadBuffer db = createBuffer(DoubleReadBuffer.class, connector, block);
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

    private void charTest(byte[] value, Connector connector, FileBlock block) {
        boolean exp;CharReadBuffer cb = createBuffer(CharReadBuffer.class, connector, block);
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


    private void shortTest(byte[] value, Connector connector, FileBlock block) {
        boolean exp;ShortReadBuffer cb = createBuffer(ShortReadBuffer.class,connector, block);
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


    private void longTest(byte[] value, Connector connector, FileBlock block) {
        boolean exp;LongReadBuffer db = createBuffer(LongReadBuffer.class,connector, block);
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

    public void testMultiThread() throws Exception{
        final byte[] value = createRandomByte();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, "0");
        EasyMock.expect(connector.read(EasyMock.eq(block))).andReturn(value).anyTimes();
        control.replay();
        final ByteReadBuffer buffer =  createBuffer(ByteReadBuffer.class, connector, block);
        Thread[] t = new Thread[100];
        for(int i = 0; i < t.length; i++){
            if((i & 1) == 0) {
                t[i] = new Thread() {
                    public void run() {
                        try {
                            byte b = 0;
                            for (int k = 0; k < value.length; k++) {
                                b += buffer.get(k);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
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
