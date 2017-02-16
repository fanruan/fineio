package com.fineio.test;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.file.*;
import com.fineio.io.read.*;
import com.fineio.io.write.WriteBuffer;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public class FineIOTest extends TestCase {

    public void testCreateReadIOFile() throws Exception {

        byte len = (byte) (Math.random() * 100d);
        byte[] res = new byte[16];
        Bits.putInt(res, 0, len * 2);
        res[8] = len;
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Field head = FileConstants.class.getDeclaredField("HEAD");
        head.setAccessible(true);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, head.get(null));
        EasyMock.expect(connector.read(EasyMock.eq(block))).andReturn(res).anyTimes();
        control.replay();
        FineIOFile file = FineIO.createIOFile(connector , u, FineIO.MODEL.READ_LONG);
        assertTrue(file instanceof FineReadIOFile);
    }

    public void testCreateWriteIOFile() throws Exception {
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        FineIOFile<WriteBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE);
        assertTrue(file instanceof FineWriteIOFile);
    }


    private byte[] createRandomByte(int len){
        byte[] arrays = new byte[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (byte)(Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }


    public void testRead() throws  Exception {
        int blocks = 4;
        int block_off_set = 23;
        int byteLen = (1 << block_off_set);
        byte[] block0 =  createRandomByte(byteLen);
        byte[] block1 =  createRandomByte(byteLen);
        byte[] block2 =  createRandomByte(byteLen);
        byte[] block3 =  createRandomByte(byteLen >> 3);
        long totalLen = (((long)byteLen) * 3 + block3.length) ;
        byte[] head = new byte[16];
        Bits.putInt(head, 0, blocks);
        head[8] = (byte) block_off_set;
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Field fieldHead = FileConstants.class.getDeclaredField("HEAD");
        fieldHead.setAccessible(true);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, fieldHead.get(null));
        EasyMock.expect(connector.read(EasyMock.eq(block))).andReturn(head).anyTimes();
        FileBlock block_0 = constructor.newInstance(u, String.valueOf(0));
        EasyMock.expect(connector.read(EasyMock.eq(block_0))).andReturn(block0).anyTimes();
        FileBlock block_1 = constructor.newInstance(u, String.valueOf(1));
        EasyMock.expect(connector.read(EasyMock.eq(block_1))).andReturn(block1).anyTimes();
        FileBlock block_2 = constructor.newInstance(u, String.valueOf(2));
        EasyMock.expect(connector.read(EasyMock.eq(block_2))).andReturn(block2).anyTimes();
        FileBlock block_3 = constructor.newInstance(u, String.valueOf(3));
        EasyMock.expect(connector.read(EasyMock.eq(block_3))).andReturn(block3).anyTimes();
        control.replay();
        FineReadIOFile<LongReadBuffer> file = (FineReadIOFile<LongReadBuffer>) FineIO.createIOFile(connector , u, FineIO.MODEL.READ_LONG);
        long v1 = 0;
        for(long i = 0, ilen = (totalLen >> 3); i < ilen; i++){
            v1 +=FineReadIOFile.getLong(file, i);
        }
        long v2 = 0;
        for(int i = 0;i < byteLen; i+=8){
            v2+= Bits.getLong(block0, i);
            v2+= Bits.getLong(block1, i);
            v2+= Bits.getLong(block2, i);
            if(i < block3.length){
                v2+= Bits.getLong(block3, i);
            }
        }
        assertEquals(v1, v2);
        FineReadIOFile<IntReadBuffer> ifile = (FineReadIOFile<IntReadBuffer>) FineIO.createIOFile(connector , u, FineIO.MODEL.READ_INT);
        v1 = 0;
        for(long i = 0, ilen = (totalLen >> 2); i < ilen; i++){
            v1 +=FineReadIOFile.getInt(ifile, i);
        }
        v2 = 0;
        for(int i = 0;i < byteLen; i+=4){
            v2+= Bits.getInt(block0, i);
            v2+= Bits.getInt(block1, i);
            v2+= Bits.getInt(block2, i);
            if(i < block3.length){
                v2+= Bits.getInt(block3, i);
            }
        }
        assertEquals(v1, v2);
        FineReadIOFile<DoubleReadBuffer> dfile = (FineReadIOFile<DoubleReadBuffer>) FineIO.createIOFile(connector , u, FineIO.MODEL.READ_DOUBLE);
        double d1 = 0;
        for(long i = 0, ilen = (totalLen >> 3); i < ilen; i++){
            d1 +=FineReadIOFile.getDouble(dfile, i);
        }
        double d2 = 0;
        for(int i = 0;i < byteLen; i+=8){
            d2+= Bits.getDouble(block0, i);
            d2+= Bits.getDouble(block1, i);
            d2+= Bits.getDouble(block2, i);
            if(i < block3.length){
                d2+= Bits.getDouble(block3, i);
            }
        }
        assertEquals(d1, d2);
    }
}
