package com.fineio.test;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.directio.DirectEditIOFile;
import com.fineio.directio.DirectIOFile;
import com.fineio.directio.DirectReadIOFile;
import com.fineio.directio.DirectWriteIOFile;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.IOSetException;
import com.fineio.io.*;
import com.fineio.io.file.*;
import com.fineio.storage.Connector;
import com.fineio.test.file.FineWriteIOTest;
import com.fineio.test.io.MemoryLeakTest;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
        MemoryLeakTest.assertZeroMemory();
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
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22);
        control.replay();
        IOFile file = FineIO.createIOFile(connector , u, FineIO.MODEL.READ_LONG);
        assertTrue(file instanceof ReadIOFile);
        file.close();
        MemoryLeakTest.assertZeroMemory();
    }

    public void testCreateWriteIOFile() throws Exception {
        Connector connector = new FineWriteIOTest.MemoryConnector();
        URI u = new URI("");
        IOFile<DoubleBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_DOUBLE);
        assertTrue(file instanceof WriteIOFile);
        file.close();
        MemoryLeakTest.assertZeroMemory();
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
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22);
        control.replay();
        IOFile<DoubleBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.EDIT_DOUBLE);
        assertTrue(file instanceof EditIOFile);
        file.close();
        MemoryLeakTest.assertZeroMemory();
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
        URI u = new URI("/");
        Connector connector = getConnector(block0, block1, block2, block3, head, u);
        u = new URI("");
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
        file.close();
        connector = getConnector(block0, block1, block2, block3, head, new URI("/"));
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

        connector = getConnector(block0, block1, block2, block3, head, new URI("/"));
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


        assertEquals(d2, d3);

        assertTrue(dfile.delete());

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
        MemoryLeakTest.assertZeroMemory();
    }

    protected Connector getConnector(byte[] block0, byte[] block1, byte[] block2, byte[] block3, byte[] head, URI u) throws NoSuchFieldException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, IOException {
        Connector connector = new FineWriteIOTest.MemoryConnector();
        Field fieldHead = FileConstants.class.getDeclaredField("HEAD");
        fieldHead.setAccessible(true);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, fieldHead.get(null));
        connector.write(block, new ByteArrayInputStream(head));

        FileBlock block_0 = constructor.newInstance(u, String.valueOf(0));
        connector.write(block_0, new ByteArrayInputStream(block0));

        FileBlock block_1 = constructor.newInstance(u, String.valueOf(1));
        connector.write(block_1, new ByteArrayInputStream(block1));

        FileBlock block_2 = constructor.newInstance(u, String.valueOf(2));
        connector.write(block_2, new ByteArrayInputStream(block2));

        FileBlock block_3 = constructor.newInstance(u, String.valueOf(3));
        connector.write(block_3, new ByteArrayInputStream(block3));
        return connector;
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
        URI u = new URI("/");
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
        u = new URI("");
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
        MemoryLeakTest.assertZeroMemory();
    }


    public void testDirectAccess() {
        Connector connector = new FineWriteIOTest.MemoryConnector();
        URI uri = URI.create("A");
        DirectWriteIOFile<DoubleBuffer> dw = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_DOUBLE_DIRECT);
        int len = 1000000;
        double[] doubles = createRandomDouble(len);
        for(int i = 0; i < len; i++) {
            FineIO.put(dw, i, doubles[i]);
        }
        dw.close();
        MemoryLeakTest.assertZeroMemory();
        DirectReadIOFile<DoubleBuffer> dr = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_DOUBLE_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals(doubles[i - 1], FineIO.getDouble(dr, i -1 ));
        }
        dr.close();
        MemoryLeakTest.assertZeroMemory();
        DirectEditIOFile<DoubleBuffer> de = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_DOUBLE_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals(doubles[i - 1], FineIO.getDouble(de, i -1 ));
        }
        FineIO.put(de, len/2, (double) 100);
        assertEquals((double)100, FineIO.getDouble(de, len/2 ));
        de.close();
        dr = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_DOUBLE_DIRECT);
        assertEquals((double)100, FineIO.getDouble(dr, len/2 ));
        dr.close();
        MemoryLeakTest.assertZeroMemory();




        DirectWriteIOFile<CharBuffer> cw = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_CHAR_DIRECT);
        for(int i = 0; i < len; i++) {
            FineIO.put(cw, i, (char)doubles[i]);
        }
        cw.close();
        MemoryLeakTest.assertZeroMemory();
        DirectReadIOFile<CharBuffer> cr = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_CHAR_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((char)doubles[i - 1], FineIO.getChar(cr, i -1 ));
        }
        cr.close();
        MemoryLeakTest.assertZeroMemory();
        DirectEditIOFile<CharBuffer> ce = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_CHAR_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((char)doubles[i - 1], FineIO.getChar(ce, i -1 ));
        }
        FineIO.put(ce, len/2, (char)100);
        assertEquals((char)100, FineIO.getChar(ce, len/2 ));
        ce.close();
        cr = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_CHAR_DIRECT);
        assertEquals((char)100, FineIO.getChar(cr, len/2 ));
        cr.close();
        MemoryLeakTest.assertZeroMemory();





        DirectWriteIOFile<LongBuffer> lw = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_LONG_DIRECT);
        for(int i = 0; i < len; i++) {
            FineIO.put(lw, i, (long)doubles[i]);
        }
        lw.close();
        MemoryLeakTest.assertZeroMemory();
        DirectReadIOFile<LongBuffer> lr = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_LONG_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((long)doubles[i - 1], FineIO.getLong(lr, i -1 ));
        }
        lr.close();
        MemoryLeakTest.assertZeroMemory();
        DirectEditIOFile<LongBuffer> le = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_LONG_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((long)doubles[i - 1], FineIO.getLong(le, i -1 ));
        }
        FineIO.put(le, len/2, (long)100);
        assertEquals((long)100, FineIO.getLong(le, len/2 ));
        le.close();
        lr = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_LONG_DIRECT);
        assertEquals((long)100, FineIO.getLong(lr, len/2 ));
        lr.close();
        MemoryLeakTest.assertZeroMemory();





        DirectWriteIOFile<IntBuffer> iw = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_INT_DIRECT);
        for(int i = 0; i < len; i++) {
            FineIO.put(iw, i, (int)doubles[i]);
        }
        iw.close();
        MemoryLeakTest.assertZeroMemory();
        DirectReadIOFile<IntBuffer> ir = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_INT_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((int)doubles[i - 1], FineIO.getInt(ir, i -1 ));
        }
        ir.close();
        MemoryLeakTest.assertZeroMemory();

        DirectEditIOFile<IntBuffer> ie = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_INT_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((int)doubles[i - 1], FineIO.getInt(ie, i -1 ));
        }
        FineIO.put(ie, len/2, (int)100);
        assertEquals((int)100, FineIO.getInt(ie, len/2 ));
        ie.close();
        ir = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_INT_DIRECT);
        assertEquals((int)100, FineIO.getInt(ir, len/2 ));
        ir.close();
        MemoryLeakTest.assertZeroMemory();





        DirectWriteIOFile<ByteBuffer> bw = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_BYTE_DIRECT);
        for(int i = 0; i < len; i++) {
            FineIO.put(bw, i, (byte)doubles[i]);
        }
        bw.close();
        MemoryLeakTest.assertZeroMemory();
        DirectReadIOFile<ByteBuffer> br = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_BYTE_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((byte)doubles[i - 1], FineIO.getByte(br, i -1 ));
        }
        br.close();
        MemoryLeakTest.assertZeroMemory();
        DirectEditIOFile<ByteBuffer> be = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_BYTE_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((byte)doubles[i - 1], FineIO.getByte(be, i -1 ));
        }
        FineIO.put(be, len/2, (byte)100);
        assertEquals((byte)100, FineIO.getByte(be, len/2 ));
        be.close();
        br = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_BYTE_DIRECT);
        assertEquals((byte)100, FineIO.getByte(br, len/2 ));
        br.close();
        MemoryLeakTest.assertZeroMemory();





        DirectWriteIOFile<ShortBuffer> sw = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_SHORT_DIRECT);
        for(int i = 0; i < len; i++) {
            FineIO.put(sw, i, (short)doubles[i]);
        }
        sw.close();
        MemoryLeakTest.assertZeroMemory();
        DirectReadIOFile<ShortBuffer> sr = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_SHORT_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((short)doubles[i - 1], FineIO.getShort(sr, i -1 ));
        }
        sr.close();
        MemoryLeakTest.assertZeroMemory();
        DirectEditIOFile<ShortBuffer> se = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_SHORT_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((short)doubles[i - 1], FineIO.getShort(se, i -1 ));
        }
        FineIO.put(se, len/2, (short)100);
        assertEquals((short)100, FineIO.getShort(se, len/2 ));
        se.close();
        sr = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_SHORT_DIRECT);
        assertEquals((short)100, FineIO.getShort(sr, len/2 ));
        sr.close();
        MemoryLeakTest.assertZeroMemory();






        DirectWriteIOFile<FloatBuffer> fw = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_FLOAT_DIRECT);
        for(int i = 0; i < len; i++) {
            FineIO.put(fw, i, (float)doubles[i]);
        }
        fw.close();
        MemoryLeakTest.assertZeroMemory();
        DirectReadIOFile<FloatBuffer> fr = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_FLOAT_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((float)doubles[i - 1], FineIO.getFloat(fr, i -1 ));
        }
        fr.close();
        MemoryLeakTest.assertZeroMemory();
        DirectEditIOFile<FloatBuffer> fe = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_FLOAT_DIRECT);
        for(int i = len; i > 0; i--) {
            assertEquals((float)doubles[i - 1], FineIO.getFloat(fe, i -1 ));
        }
        FineIO.put(fe, len/2, 100);
        assertEquals((float)100, FineIO.getFloat(fe, len/2 ));
        fe.close();
        fr = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_FLOAT_DIRECT);
        assertEquals((float)100, FineIO.getFloat(fr, len/2 ));
        fr.close();
        MemoryLeakTest.assertZeroMemory();
    }
}
