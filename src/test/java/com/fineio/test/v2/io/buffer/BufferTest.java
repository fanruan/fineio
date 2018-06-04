package com.fineio.test.v2.io.buffer;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.AbstractBuffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.read.ByteReadBuffer;
import com.fineio.io.read.CharReadBuffer;
import com.fineio.io.read.DoubleReadBuffer;
import com.fineio.io.read.FloatReadBuffer;
import com.fineio.io.read.IntReadBuffer;
import com.fineio.io.read.LongReadBuffer;
import com.fineio.io.read.ShortReadBuffer;
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
    private static <T extends AbstractBuffer> T createBuffer(Class<T> clazz, Object connector, Object block, int offset) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
            constructor.setAccessible(true);
            return constructor.newInstance(connector, block, offset);
        } catch (Exception e) {
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
        ByteBuffer buffer = createBuffer(ByteBuffer.class, connector, block, off);
        ByteReadBuffer readOnlyBuffer = buffer.readOnlyBuffer();
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
        readOnlyBuffer.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    private void intTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        IntBuffer ib = createBuffer(IntBuffer.class, connector, block, off - 2);
        IntReadBuffer readOnlyBuffer = ib.readOnlyBuffer();
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
        readOnlyBuffer.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    private void floatTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        FloatBuffer ib = createBuffer(FloatBuffer.class, connector, block, off - 2);
        FloatReadBuffer readOnlyBuffer = ib.readOnlyBuffer();
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
        readOnlyBuffer.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    private void doubleTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        DoubleBuffer db = createBuffer(DoubleBuffer.class, connector, block, off - 3);
        DoubleReadBuffer readOnlyBuffer = db.readOnlyBuffer();
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
        readOnlyBuffer.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    private void charTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        CharBuffer cb = createBuffer(CharBuffer.class, connector, block, off - 1);
        CharReadBuffer readOnlyBuffer = cb.readOnlyBuffer();
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
        readOnlyBuffer.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    private void shortTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        ShortBuffer cb = createBuffer(ShortBuffer.class, connector, block, off - 1);
        ShortReadBuffer readOnlyBuffer = cb.readOnlyBuffer();
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
        readOnlyBuffer.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


    private void longTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;
        LongBuffer db = createBuffer(LongBuffer.class, connector, block, off - 3);
        LongReadBuffer readOnlyBuffer = db.readOnlyBuffer();
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
        readOnlyBuffer.close();
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
        final ByteBuffer buffer = createBuffer(ByteBuffer.class, connector, block, 10);
        final ByteReadBuffer byteReadOnlyBuffer = buffer.readOnlyBuffer();
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
                            byteReadOnlyBuffer.clear();
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
            byteReadOnlyBuffer.close();
        }
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);

    }
}