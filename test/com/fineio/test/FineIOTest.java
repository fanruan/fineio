package com.fineio.test;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.IOSetException;
import com.fineio.io.file.*;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Arrays;

/**
 * Created by daniel on 2017/2/9.
 */
public class FineIOTest extends TestCase {


    public void testThreads() {
        int counts = Runtime.getRuntime().availableProcessors() + 1;
        assertEquals(counts, FineIO.getSyncThreads());
        FineIO.setSyncThreads(30);
        assertEquals(30, FineIO.getSyncThreads());
        boolean exp =false;
        try {
            FineIO.setSyncThreads(0);
        }catch (IOSetException e){
            exp = true;
        }
        assertTrue(exp);
        assertEquals(30, FineIO.getSyncThreads());
        exp =false;
        try {
            FineIO.setSyncThreads(-1);
        }catch (IOSetException e){
            exp = true;
        }
        assertTrue(exp);
        assertEquals(30, FineIO.getSyncThreads());
    }

    public void testCreateReadIOFile() throws Exception {

        byte len = (byte) (Math.random() * 100d);
        final byte[] res = new byte[16];
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
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() throws Throwable {
                return  new ByteArrayInputStream(res);
            }
        }).anyTimes();
        control.replay();
        IOFile file = FineIO.createIOFile(connector , u, FineIO.MODEL.READ_LONG);
        assertTrue(file instanceof ReadIOFile);
    }

    public void testCreateWriteIOFile() throws Exception {
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        IOFile<DoubleBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_DOUBLE);
        assertTrue(file instanceof WriteIOFile);
    }

    public void testCreateEditIOFile() throws Exception {
        byte len = (byte) (Math.random() * 100d);
        final byte[] res = new byte[16];
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
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            
            public InputStream answer() throws Throwable {
                return  new ByteArrayInputStream(res);
            }
        }).anyTimes();
        control.replay();
        IOFile<DoubleBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.EDIT_DOUBLE);
        assertTrue(file instanceof EditIOFile);
    }


    private byte[] createRandomByte(int len){
        byte[] arrays = new byte[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (byte)(Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    public void testEdit() throws  Exception {
        int blocks = 4;
        int block_off_set = 23;
        int byteLen = (1 << block_off_set);
        final byte[] block0 =  createRandomByte(byteLen);
        final byte[] block1 =  createRandomByte(byteLen);
        final byte[] block2 =  createRandomByte(byteLen);
        final byte[] block3 =  createRandomByte(byteLen >> 3);
        long totalLen = (((long)byteLen) * 3 + block3.length) ;
        final byte[] head = new byte[16];
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
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(head);
            }
        }).anyTimes();
        FileBlock block_0 = constructor.newInstance(u, String.valueOf(0));
        EasyMock.expect(connector.read(EasyMock.eq(block_0))).andAnswer(new IAnswer<InputStream>() {
            
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(block0);
            }
        }).anyTimes();
        FileBlock block_1 = constructor.newInstance(u, String.valueOf(1));
        EasyMock.expect(connector.read(EasyMock.eq(block_1))).andAnswer(new IAnswer<InputStream>() {
            
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(block1);
            }
        }).anyTimes();
        FileBlock block_2 = constructor.newInstance(u, String.valueOf(2));
        EasyMock.expect(connector.read(EasyMock.eq(block_2))).andAnswer(new IAnswer<InputStream>() {
            
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(block2);
            }
        }).anyTimes();
        FileBlock block_3 = constructor.newInstance(u, String.valueOf(3));
        EasyMock.expect(connector.read(EasyMock.eq(block_3))).andAnswer(new IAnswer<InputStream>() {
            
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(block3);
            }
        }).anyTimes();

        final boolean[] deleteArray = new boolean[6];
        Arrays.fill(deleteArray, false);
        EasyMock.expect(connector.delete(EasyMock.eq(block))).andAnswer(new IAnswer<Boolean>() {

            public Boolean answer() throws Throwable {
                deleteArray[5] = true;
                return true;
            }
        }).anyTimes();
        EasyMock.expect(connector.delete(EasyMock.eq(block_0))).andAnswer(new IAnswer<Boolean>() {

            public Boolean answer() throws Throwable {
                deleteArray[0] = true;
                return  true;
            }
        }).anyTimes();
        EasyMock.expect(connector.delete(EasyMock.eq(block_1))).andAnswer(new IAnswer<Boolean>() {

            public Boolean answer() throws Throwable {
                deleteArray[1] = true;
                return true;
            }
        }).anyTimes();
        EasyMock.expect(connector.delete(EasyMock.eq(block_2))).andAnswer(new IAnswer<Boolean>() {

            public Boolean answer() throws Throwable {
                deleteArray[2] = true;
                return false;
            }
        }).anyTimes();
        EasyMock.expect(connector.delete(EasyMock.eq(block_3))).andAnswer(new IAnswer<Boolean>() {

            public Boolean answer() throws Throwable {
                deleteArray[3] = true;
                return true;
            }
        }).anyTimes();
        FileBlock block_folder = constructor.newInstance(u, "");
        EasyMock.expect(connector.delete(EasyMock.eq(block_folder))).andAnswer(new IAnswer<Boolean>() {

            public Boolean answer() throws Throwable {
                deleteArray[4] = true;
                return true;
            }
        }).anyTimes();


        EasyMock.expect(connector.delete((FileBlock) EasyMock.anyObject())).andAnswer(new IAnswer<Boolean>() {

            public Boolean answer() throws Throwable {
                return true;
            }
        }).anyTimes();

        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(InputStream.class));
        EasyMock.expectLastCall().anyTimes();
        control.replay();
        EditIOFile<LongBuffer> file =  FineIO.createIOFile(connector , u, FineIO.MODEL.EDIT_LONG);
        long v1 = 0;
        for(long i = 0, ilen = (totalLen >> 3); i < ilen; i++){
            v1 += FineIO.getLong(file, i);
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
        long currentLen = totalLen >> 3;
        boolean exp = false;
        try {
            FineIO.getLong(file, currentLen);
        }catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        for(long i = currentLen; i < currentLen*2; i++){
            FineIO.put(file, i, FineIO.getLong(file, i - currentLen));
        }
        long v3 = 0;
        for(long i = currentLen, ilen = currentLen*2; i < ilen; i++){
            v3 += FineIO.getLong(file, i);
        }
        assertEquals(v2, v3);
        EditIOFile<IntBuffer> ifile =   FineIO.createIOFile(connector , u, FineIO.MODEL.EDIT_INT);
        v1 = 0;
        for(long i = 0, ilen = (totalLen >> 2); i < ilen; i++){
            v1 += FineIO.getInt(ifile, i);
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
        currentLen = totalLen >> 2;
        exp = false;
        try {
            FineIO.getInt(ifile, currentLen);
        }catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        for(long i = currentLen; i < currentLen*2; i++){
            FineIO.put(ifile, i, FineIO.getInt(ifile, i - currentLen));
        }
        v3 = 0;
        for(long i = currentLen, ilen = currentLen*2; i < ilen; i++){
            v3 += FineIO.getInt(ifile, i);
        }
        assertEquals(v2, v3);


        EditIOFile<DoubleBuffer> dfile =  FineIO.createIOFile(connector , u, FineIO.MODEL.EDIT_DOUBLE);
        double d1 = 0;
        for(long i = 0, ilen = (totalLen >> 3); i < ilen; i++){
            d1 += FineIO.getDouble(dfile, i);
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

        currentLen = totalLen >> 3;
        exp = false;
        try {
            FineIO.getDouble(dfile, currentLen);
        }catch (BufferIndexOutOfBoundsException e) {
            exp = true;
        }
        assertTrue(exp);
        for(long i = currentLen; i < currentLen*2; i++){
            FineIO.put(dfile, i, FineIO.getDouble(dfile, i - currentLen));
        }
        double d3 = 0;
        for(long i = currentLen, ilen = currentLen*2; i < ilen; i++){
            d3 += FineIO.getDouble(dfile, i);
        }
        for(boolean b : deleteArray){
            assertFalse(b);
        }

        assertEquals(d2, d3);

        assertFalse(dfile.delete());
        for(boolean b : deleteArray){
            assertTrue(b);
        }
        assertTrue(FineIO.getCurrentMemorySize()>0);
        assertTrue(FineIO.getCurrentReadMemorySize() > 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        file.close();
        ifile.close();
        dfile.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);

    }


    public void testRead() throws  Exception {
        int blocks = 4;
        int block_off_set = 23;
        int byteLen = (1 << block_off_set);
        final byte[] block0 =  createRandomByte(byteLen);
        final byte[] block1 =  createRandomByte(byteLen);
        final byte[] block2 =  createRandomByte(byteLen);
        final byte[] block3 =  createRandomByte(byteLen >> 3);
        long totalLen = (((long)byteLen) * 3 + block3.length) ;
        final byte[] head = new byte[16];
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
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(head);
            }
        }).anyTimes();
        FileBlock block_0 = constructor.newInstance(u, String.valueOf(0));
        EasyMock.expect(connector.read(EasyMock.eq(block_0))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(block0);
            }
        }).anyTimes();
        FileBlock block_1 = constructor.newInstance(u, String.valueOf(1));
        EasyMock.expect(connector.read(EasyMock.eq(block_1))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(block1);
            }
        }).anyTimes();
        FileBlock block_2 = constructor.newInstance(u, String.valueOf(2));
        EasyMock.expect(connector.read(EasyMock.eq(block_2))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(block2);
            }
        }).anyTimes();
        FileBlock block_3 = constructor.newInstance(u, String.valueOf(3));
        EasyMock.expect(connector.read(EasyMock.eq(block_3))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(block3);
            }
        }).anyTimes();
        control.replay();
        ReadIOFile<LongBuffer> file = FineIO.createIOFile(connector , u, FineIO.MODEL.READ_LONG);
        long v1 = 0;
        for(long i = 0, ilen = (totalLen >> 3); i < ilen; i++){
            v1 += FineIO.getLong(file, i);
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
        ReadIOFile<IntBuffer> ifile =  FineIO.createIOFile(connector , u, FineIO.MODEL.READ_INT);
        v1 = 0;
        for(long i = 0, ilen = (totalLen >> 2); i < ilen; i++){
            v1 += FineIO.getInt(ifile, i);
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
        ReadIOFile<DoubleBuffer> dfile = FineIO.createIOFile(connector , u, FineIO.MODEL.READ_DOUBLE);
        double d1 = 0;
        for(long i = 0, ilen = (totalLen >> 3); i < ilen; i++){
            d1 += FineIO.getDouble(dfile, i);
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
        assertTrue(FineIO.getCurrentMemorySize()>0);
        assertTrue(FineIO.getCurrentReadMemorySize() > 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        file.close();
        ifile.close();
        dfile.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    private double[] createRandomDouble(int len){
        double[] arrays = new double[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (Math.random() * 100000000000d);
        }
        return arrays;
    }

    public void testWrite() throws Exception {
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(InputStream.class));
        EasyMock.expectLastCall().anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().anyTimes();
        control.replay();
        WriteIOFile<DoubleBuffer> dfile =  FineIO.createIOFile(connector , u, FineIO.MODEL.WRITE_DOUBLE);
        int len = 1000000;
        double[] doubles = createRandomDouble(len);
        for(int i = 0; i< doubles.length; i++) {
            FineIO.put(dfile, i, doubles[i]);
        }

        WriteIOFile<DoubleBuffer> dfile2 =  FineIO.createIOFile(connector , u, FineIO.MODEL.WRITE_DOUBLE);
        for(int i = 0; i< doubles.length; i++) {
            FineIO.put(dfile2, doubles[i]);
        }
        WriteIOFile<DoubleBuffer> dfile3=  FineIO.createIOFile(connector , u, FineIO.MODEL.WRITE_DOUBLE);
        FineIO.put(dfile3, 0, doubles[0]);
        for(int i = 1; i< doubles.length; i++) {
            FineIO.put(dfile3, doubles[i]);
        }
        assertTrue(FineIO.getCurrentMemorySize()>0);
        assertEquals(FineIO.getCurrentReadMemorySize() , 0);
        assertTrue(FineIO.getCurrentWriteMemorySize() > 0);

        dfile.close();
        dfile2.close();
        dfile3.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }


}
