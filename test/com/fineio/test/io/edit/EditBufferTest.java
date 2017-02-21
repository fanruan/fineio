package com.fineio.test.io.edit;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.file.FileConstants;
import com.fineio.file.IOFile;
import com.fineio.io.AbstractBuffer;
import com.fineio.io.edit.*;
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
 * Created by daniel on 2017/2/21.
 */
public class EditBufferTest extends TestCase {

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
        IOFile editIOFile = FineIO.createIOFile(connector, u, FineIO.MODEL.EDIT_LONG);
        ByteEditBuffer byteEditBuffer = getEditBuffer(editIOFile, ByteEditBuffer.class );
        Method method = ByteEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        int v = (Integer) method.invoke(byteEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_BYTE);
        DoubleEditBuffer doubleEditBuffer = getEditBuffer(editIOFile, DoubleEditBuffer.class);
        method = DoubleEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(doubleEditBuffer);
        LongEditBuffer longEditBuffer = getEditBuffer(editIOFile, LongEditBuffer.class );
        method = LongEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(longEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_LONG);
        IntEditBuffer intEditBuffer = getEditBuffer(editIOFile, IntEditBuffer.class );
        method = IntEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(intEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_INT);
        CharEditBuffer charEditBuffer = getEditBuffer(editIOFile, CharEditBuffer.class );
        method = CharEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(charEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_CHAR);
        FloatEditBuffer floatEditBuffer = getEditBuffer(editIOFile, FloatEditBuffer.class );
        method = FloatEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(floatEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_FLOAT);

        ShortEditBuffer shortEditBuffer = getEditBuffer(editIOFile, ShortEditBuffer.class );
        method = ShortEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(shortEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_SHORT);

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


    private static <T extends EditBuffer> T getEditBuffer(IOFile<EditBuffer> EditIOFile, Class<T> clazz) {
        try {
            Method method = IOFile.class.getDeclaredMethod("createBuffer", Class.class, int.class);
            method.setAccessible(true);
            return (T) method.invoke(EditIOFile, clazz, 0);
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
        byteTest(value, connector, block, len*2);
        intTest(value, connector, block, len*2);
        doubleTest(value, connector, block, len*2);
        charTest(value, connector, block, len*2);
        shortTest(value, connector, block, len*2);
        longTest(value, connector, block, len*2);
        floatTest(value, connector, block, len*2);
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
        try {
            buffer.get(1 << off);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
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
        }
        assertEquals(v1, v2);
        exp = false;
        try {
            ib.get(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
    }


    private void floatTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;FloatEditBuffer ib = createBuffer(FloatEditBuffer.class, connector, block, off -2);
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
    }

    private void doubleTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;DoubleEditBuffer db = createBuffer(DoubleEditBuffer.class, connector, block, off -3);
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
    }

    private void charTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;CharEditBuffer cb = createBuffer(CharEditBuffer.class, connector, block, off -1);
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
    }


    private void shortTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;ShortEditBuffer cb = createBuffer(ShortEditBuffer.class,connector, block, off -1);
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
    }


    private void longTest(byte[] value, Connector connector, FileBlock block, int off) {
        boolean exp;LongEditBuffer db = createBuffer(LongEditBuffer.class,connector, block, off - 3);
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
    }
}
