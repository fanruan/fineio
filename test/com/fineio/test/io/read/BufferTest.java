package com.fineio.test.io.read;

import com.fineio.base.Bits;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.io.read.ByteReadBuffer;
import com.fineio.io.read.CharReadBuffer;
import com.fineio.io.read.DoubleReadBuffer;
import com.fineio.io.read.IntReadBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import sun.misc.Unsafe;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by daniel on 2017/2/13.
 */
public class BufferTest  extends TestCase {

    private byte[] createRandomByte(){
        int len = 1 << 10;
        byte[] arrays = new byte[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (byte)(Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
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
    }

    private void byteTest(byte[] value, Connector connector, FileBlock block) {
        ByteReadBuffer buffer = new ByteReadBuffer(connector, block);
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
        boolean exp;IntReadBuffer ib = new IntReadBuffer(connector, block);
        int v1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=4){
            v1 += Bits.getInt(value, k);
        }
        int v2 = 0;
        for(int k = 0, klen = (value.length >> 2); k < klen; k++){
            v2 +=ib.getInt(k);
        }
        assertEquals(v1, v2);
        exp = false;
        try {
            ib.getInt(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        ib.clear();
    }

    private void doubleTest(byte[] value, Connector connector, FileBlock block) {
        boolean exp;DoubleReadBuffer db = new DoubleReadBuffer(connector, block);
        double d1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=8){
            d1 += Bits.getDouble(value, k);
        }
        double d2 = 0;
        for(int k = 0, klen = (value.length >> 3); k < klen; k++){
            d2 +=db.getDouble(k);
        }
        assertEquals(d1, d2);
        exp = false;
        try {
            db.getDouble(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        db.clear();
    }

    private void charTest(byte[] value, Connector connector, FileBlock block) {
        boolean exp;CharReadBuffer cb = new CharReadBuffer(connector, block);
        char c1 = 0;
        for(int k = 0, klen = value.length; k < klen; k+=2){
            c1 += Bits.getChar(value, k);
        }
        char c2 = 0;
        for(int k = 0, klen = (value.length >> 1); k < klen; k++){
            c2 +=cb.getChar(k);
        }
        assertEquals(c1, c2);
        exp = false;
        try {
            cb.getChar(value.length);
        } catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        cb.clear();
    }

}
