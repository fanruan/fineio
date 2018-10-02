package com.fineio.test.v2.io.buffer;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.cache.CacheManager;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


/**
 * @author yee
 * @date 2018/6/1
 */
public class BufferTest {
    private byte[] createRandomByte(int off) {
        int len = 1 << off;
        byte[] arrays = new byte[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (byte) (Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    @Test
    public void testBuffer100() throws Exception {
        for (int i = 0; i < 100; i++) {
            testBuffer();
        }
    }

    @Test
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
            public InputStream answer() {
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

    private void byteTest(byte[] value, Connector connector, FileBlock block, int off) {
        ByteBuffer buffer = CacheManager.DataType.BYTE.createBuffer(connector, block, off);
        ByteBuffer.ByteReadBuffer readOnlyBuffer = buffer.asRead();
        byte r = 0;
        for (int k = 0; k < value.length; k++) {
            r += value[k];
        }
        byte r2 = 0;
        for (int k = 0; k < value.length; k++) {
            r2 += readOnlyBuffer.get(k);
        }
        assertEquals(r, r2);
        boolean exp = false;
        try {
            readOnlyBuffer.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
//        buffer.clear();
        readOnlyBuffer.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    private void intTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        IntBuffer ib = CacheManager.DataType.INT.createBuffer(connector, block, off);
        IntBuffer.IntReadBuffer readOnlyBuffer = ib.asRead();
        int v1 = 0;
        for (int k = 0, klen = value.length; k < klen; k += 4) {
            v1 += Bits.getInt(value, k);
        }
        int v2 = 0;
        for (int k = 0, klen = (value.length >> 2); k < klen; k++) {
            v2 += readOnlyBuffer.get(k);
        }
        assertEquals(v1, v2);
        exp = false;
        try {
            readOnlyBuffer.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
//        ib.clear();
        readOnlyBuffer.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    private void floatTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        FloatBuffer ib = CacheManager.DataType.FLOAT.createBuffer(connector, block, off);
        FloatBuffer.FloatReadBuffer readOnlyBuffer = ib.asRead();
        float v1 = 0;
        for (int k = 0, klen = value.length; k < klen; k += 4) {
            v1 += Bits.getFloat(value, k);
        }
        float v2 = 0;
        for (int k = 0, klen = (value.length >> 2); k < klen; k++) {
            v2 += readOnlyBuffer.get(k);
        }
        assertEquals(v1, v2);
        exp = false;
        try {
            readOnlyBuffer.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
//        ib.clear();
        readOnlyBuffer.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    private void doubleTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        DoubleBuffer db = CacheManager.DataType.DOUBLE.createBuffer(connector, block, off);
        DoubleBuffer.DoubleReadBuffer readOnlyBuffer = db.asRead();
        double d1 = 0;
        for (int k = 0, klen = value.length; k < klen; k += 8) {
            d1 += Bits.getDouble(value, k);
        }
        double d2 = 0;
        for (int k = 0, klen = (value.length >> 3); k < klen; k++) {
            d2 += readOnlyBuffer.get(k);
        }
        assertEquals(d1, d2);
        exp = false;
        try {
            readOnlyBuffer.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
//        db.clear();
//        db.force();
        readOnlyBuffer.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    private void charTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        CharBuffer cb = CacheManager.DataType.CHAR.createBuffer(connector, block, off);
        CharBuffer.CharReadBuffer readOnlyBuffer = cb.asRead();
        char c1 = 0;
        for (int k = 0, klen = value.length; k < klen; k += 2) {
            c1 += Bits.getChar(value, k);
        }
        char c2 = 0;
        for (int k = 0, klen = (value.length >> 1); k < klen; k++) {
            c2 += readOnlyBuffer.get(k);
        }
        assertEquals(c1, c2);
        exp = false;
        try {
            readOnlyBuffer.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
//        cb.clear();
//        cb.force();
        readOnlyBuffer.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    private void shortTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        ShortBuffer cb = CacheManager.DataType.SHORT.createBuffer(connector, block, off);
        ShortBuffer.ShortReadBuffer readOnlyBuffer = cb.asRead();
        short c1 = 0;
        for (int k = 0, klen = value.length; k < klen; k += 2) {
            c1 += Bits.getChar(value, k);
        }
        short c2 = 0;
        for (int k = 0, klen = (value.length >> 1); k < klen; k++) {
            c2 += readOnlyBuffer.get(k);
        }
        assertEquals(c1, c2);
        exp = false;
        try {
            readOnlyBuffer.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
//        cb.clear();
//        cb.force();
        readOnlyBuffer.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    private void longTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        LongBuffer db = CacheManager.DataType.LONG.createBuffer(connector, block, off);
        LongBuffer.LongReadBuffer readOnlyBuffer = db.asRead();
        long d1 = 0;
        for (int k = 0, klen = value.length; k < klen; k += 8) {
            d1 += Bits.getLong(value, k);
        }
        long d2 = 0;
        for (int k = 0, klen = (value.length >> 3); k < klen; k++) {
            d2 += readOnlyBuffer.get(k);
        }
        assertEquals(d1, d2);
        exp = false;
        try {
            readOnlyBuffer.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
//        db.clear();
//        db.force();
        readOnlyBuffer.clearAfterClose();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testThread() throws Exception {
        int i = 0;
        while (i++ < 10) {
            testMultiThread();
        }
    }

    @Test
    public void testMultiThread() throws Exception {
        final byte[] value = createRandomByte(10);
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, "0");
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {

            public InputStream answer() {
                return new ByteArrayInputStream(value);
            }
        }).anyTimes();
        control.replay();
        final ByteBuffer buffer = CacheManager.DataType.BYTE.createBuffer(connector, block, 10);
        final ByteBuffer.ByteReadBuffer byteReadOnlyBuffer = buffer.asRead();
        Thread[] t = new Thread[1000];
        for (int i = 0; i < t.length; i++) {
            if ((i & 1) == 0) {
                t[i] = new Thread() {
                    public void run() {
                        byte b = 0;
                        for (int k = 0; k < value.length; k++) {
                            b += byteReadOnlyBuffer.get(k);
                        }
                    }
                };
            } else {
                t[i] = new Thread() {
                    public void run() {
                        for (int k = 0; k < value.length; k++) {
                            byteReadOnlyBuffer.close();
                        }
                    }
                };
            }
        }
        for (int i = 0; i < t.length; i++) {
            t[i].start();
        }
        for (int i = 0; i < t.length; i++) {
            t[i].join();
        }
        for (int k = 0; k < value.length; k++) {
            byteReadOnlyBuffer.clearAfterClose();
        }
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);

    }
}