package com.fineio.test.io.edit;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.file.FileConstants;
import com.fineio.file.IOFile;
import com.fineio.io.base.AbstractBuffer;
import com.fineio.io.edit.*;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;
import com.fineio.test.file.FineWriteIOTest;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by daniel on 2017/2/21.
 */
public class EditBufferTest extends TestCase {



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

        Connector connector = new FineWriteIOTest.MemoryConnector();
        URI u = new URI("test");
        int len = 10;
        final byte[] value = createRandomByte(len);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, "0");
        connector.write(block, value);
        byteTest(value, connector, block, len+1);
        connector.write(block, value);
        intTest(value, connector, block, len+1);
        connector.write(block, value);
        doubleTest(value, connector, block, len+1);
        connector.write(block, value);
        charTest(value, connector, block, len+1);
        connector.write(block, value);
        shortTest(value, connector, block, len+1);
        connector.write(block, value);
        longTest(value, connector, block, len+1);
        connector.write(block, value);
        floatTest(value, connector, block, len+1);
        connector.write(block, value);
        testReloadData(value, connector, block, len+1);
        connector.write(block, value);
        testThreadReloadData(value, connector, block, len+1);
    }

    private void testReloadData(byte[] value, Connector connector, FileBlock block, int off) {
        ByteEditBuffer buffer = createBuffer(ByteEditBuffer.class, connector, block, off);
        for(int k = value.length/2; k < value.length; k++){
            buffer.put(k, (byte) 0);
        }
        for(int k = 0; k <  value.length/2; k++){
            assertEquals(buffer.get(k), value[k]);
        }

        for(int k = value.length/2; k < value.length; k++){
            assertEquals(buffer.get(k), (byte)0);
        }

    }


    private void testThreadReloadData(byte[] value, Connector connector, final FileBlock block, int off) {
        final ByteEditBuffer buffer = createBuffer(ByteEditBuffer.class, connector, block, off);
        final  AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        Thread thread = new Thread() {
            public void run(){
                while (atomicBoolean.get()) {
                    buffer.clear();
                }
            }
        };

        thread.start();
        for(int k = value.length/2; k < value.length; k++){
            buffer.put(k, (byte) 0);
        }
        atomicBoolean.set(false);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int k = 0; k <  value.length/2; k++){
            assertEquals(buffer.get(k), value[k]);
        }
        for(int k = value.length/2; k < value.length; k++){
            assertEquals(buffer.get(k), 0);
        }
        buffer.clear();
    }

    private void byteTest(byte[] value, Connector connector, FileBlock block, int off) {
        ByteEditBuffer buffer = createBuffer(ByteEditBuffer.class, connector, block, off);
        byte r = 0;
        for(int k = 0; k < value.length; k++){
            r +=value[k];
        }
        byte r2 = 0;
        for(int k = 0; k < value.length; k++){
            r2 +=buffer.get(k);
            buffer.put(k, buffer.get(k));
        }
        assertFalse(buffer.hasChanged());
        for(int k = 0; k < value.length; k++){
            buffer.put(value.length + k, value[value.length - k- 1]);
        }
        assertTrue(buffer.hasChanged());
        byte r3 = 0;
        for(int k = 0; k < value.length; k++){
            r3 +=buffer.get(value.length + k);
        }
        assertEquals(r, r2);
        assertEquals(r, r3);
        boolean exp = false;
        try {
            buffer.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(exp);
        exp = false;
        try {
            buffer.get(1 << off);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        try {
            buffer.put(1 << off, (byte) 0);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        buffer.clear();
        exp = false;
        try {
            buffer.put(0, (byte) 1);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(exp);
        exp = false;
        try {
            buffer.get(0);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(exp);
        buffer.clear();
    }

    private void intTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;IntEditBuffer ib = createBuffer(IntEditBuffer.class, connector, block, off -2);
        int v1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=4){
            v1 += Bits.getInt(value, k);
        }
        int v2 = 0;
        for(int k = 0, klen = (value.length >> 2); k < klen; k++){
            v2 +=ib.get(k);
            ib.put(k, ib.get(k));
        }
        assertFalse(ib.hasChanged());
        int klen = (value.length >> 2);
        for(int k = 0; k < klen; k++){
            ib.put(klen+ k, ib.get(klen - k- 1));
        }
        assertTrue(ib.hasChanged());
        int v3 = 0;
        for(int k = 0; k < klen; k++){
            v3 +=ib.get(klen + k);
        }
        assertEquals(v1, v2);
        assertEquals(v1, v3);
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(exp);
        exp = false;
        try {
            ib.get(1 << (off - 2));
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        try {
            ib.put(1 << (off - 2), 0);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        ib.clear();
    }


    private void floatTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;FloatEditBuffer ib = createBuffer(FloatEditBuffer.class, connector, block, off -2);
        float v1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=4){
            v1 += Bits.getFloat(value, k);
        }
        float v2 = 0;
        int klen = (value.length >> 2);
        for(int k = 0; k < klen; k++){
            v2 +=ib.get(k);
            ib.put(k, ib.get(k));
        }
        assertFalse(ib.hasChanged());
        assertEquals(v1, v2);
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(ib.hasChanged());
        assertTrue(exp);
        for(int k = 0; k < klen; k++){
            ib.put(klen+ k, ib.get(klen - k- 1));
        }
        assertTrue(ib.hasChanged());
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(exp);
        float v3 = 0;
        for(int k = klen; k > 0; k--){
            v3 +=ib.get(klen + k - 1);
        }
        assertEquals(v1, v3);
        try {
            ib.get(1 << (off - 2));
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        try {
            ib.put(1 << (off - 2), 0);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        ib.clear();
    }

    private void doubleTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;DoubleEditBuffer ib = createBuffer(DoubleEditBuffer.class, connector, block, off -3);
        double v1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=8){
            v1 += Bits.getDouble(value, k);
        }
        double v2 = 0;
        int klen = (value.length >> 3);
        for(int k = 0; k < klen; k++){
            v2 +=ib.get(k);
            ib.put(k, ib.get(k));
        }
        assertFalse(ib.hasChanged());
        assertEquals(v1, v2);
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(ib.hasChanged());
        assertTrue(exp);
        for(int k = 0; k < klen; k++){
            ib.put(klen+ k, ib.get(klen - k- 1));
        }
        assertTrue(ib.hasChanged());
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(exp);
        double v3 = 0;
        for(int k = klen; k > 0; k--){
            v3 +=ib.get(klen + k - 1);
        }
        assertEquals(v1, v3);
        try {
            ib.get(1 << (off - 3));
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        try {
            ib.put(1 << (off - 3), 0);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        ib.clear();
    }

    private void charTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;CharEditBuffer ib = createBuffer(CharEditBuffer.class, connector, block, off -1);
        char v1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=2){
            v1 += Bits.getShort(value, k);
        }
        char v2 = 0;
        int klen = (value.length >> 1);
        for(int k = 0; k < klen; k++){
            v2 +=ib.get(k);
            ib.put(k, ib.get(k));
        }
        assertFalse(ib.hasChanged());
        assertEquals(v1, v2);
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(ib.hasChanged());
        assertTrue(exp);
        for(int k = 0; k < klen; k++){
            ib.put(klen+ k, ib.get(klen - k- 1));
        }
        assertTrue(ib.hasChanged());
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(exp);
        char v3 = 0;
        for(int k = klen; k > 0; k--){
            v3 +=ib.get(klen + k - 1);
        }
        assertEquals(v1, v3);
        try {
            ib.get(1 << (off - 1));
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        try {
            ib.put(1 << (off - 1), (char)0);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        ib.clear();
    }


    private void shortTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;ShortEditBuffer ib = createBuffer(ShortEditBuffer.class,connector, block, off -1);
        short v1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=2){
            v1 += Bits.getShort(value, k);
        }
        short v2 = 0;
        int klen = (value.length >> 1);
        for(int k = 0; k < klen; k++){
            v2 +=ib.get(k);
            ib.put(k, ib.get(k));
        }
        assertFalse(ib.hasChanged());
        assertEquals(v1, v2);
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(ib.hasChanged());
        assertTrue(exp);
        for(int k = 0; k < klen; k++){
            ib.put(klen+ k, ib.get(klen - k- 1));
        }
        assertTrue(ib.hasChanged());
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(exp);
        short v3 = 0;
        for(int k = klen; k > 0; k--){
            v3 +=ib.get(klen + k - 1);
        }
        assertEquals(v1, v3);
        try {
            ib.get(1 << (off - 1));
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        try {
            ib.put(1 << (off - 1), (short)0);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        ib.clear();
    }


    private void longTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;LongEditBuffer ib = createBuffer(LongEditBuffer.class,connector, block, off - 3);
        long v1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=8){
            v1 += Bits.getLong(value, k);
        }
        long v2 = 0;
        int klen = (value.length >> 3);
        for(int k = 0; k < klen; k++){
            v2 +=ib.get(k);
            ib.put(k, ib.get(k));
        }
        assertFalse(ib.hasChanged());
        assertEquals(v1, v2);
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(ib.hasChanged());
        assertTrue(exp);
        for(int k = 0; k < klen; k++){
            ib.put(klen+ k, ib.get(klen - k- 1));
        }
        assertTrue(ib.hasChanged());
        exp = false;
        try {
            ib.get(klen);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertFalse(exp);
        long v3 = 0;
        for(int k = klen; k > 0; k--){
            v3 +=ib.get(klen + k - 1);
        }
        assertEquals(v1, v3);
        try {
            ib.get(1 << (off - 3));
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        try {
            ib.put(1 << (off - 3), 0);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        ib.clear();
    }
}
