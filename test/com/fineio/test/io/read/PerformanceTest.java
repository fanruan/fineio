package com.fineio.test.io.read;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.file.FileBlock;
import com.fineio.file.FileConstants;
import com.fineio.file.ReadIOFile;
import com.fineio.io.AbstractBuffer;
import com.fineio.io.read.ByteReadBuffer;
import com.fineio.io.read.DoubleReadBuffer;
import com.fineio.io.read.IntReadBuffer;
import com.fineio.storage.Connector;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;

/**
 * Created by daniel on 2017/2/13.
 */
public class PerformanceTest {


    private static  byte[] createRandomByte(int len){
        byte[] arrays = new byte[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (byte) (Math.random()*1000000000d);
        }
        return arrays;
    }

    public static void main(String[] args) throws Exception {
//        Thread.sleep(20000);
//        createFile();
        doubleSumFileTest();
//        final byte[] bytes  = createRandomByte(1 << 30);
//        IMocksControl control = EasyMock.createControl();
//        Connector connector = control.createMock(Connector.class);
//        URI u = new URI("");
//        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
//        constructor.setAccessible(true);
//        FileBlock block = constructor.newInstance(u, "0");
//        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
//            @Override
//            public InputStream answer() throws Throwable {
//                return new ByteArrayInputStream(bytes);
//            }
//        }).anyTimes();
//        control.replay();
//        byteTest(bytes, connector, block);
//        intTest(bytes, connector, block);
//        doubleTest(bytes, connector, block);
//        doubleSumTest();
//        while (true) {
//            Thread.sleep(10000);
//        }
    }

    private static void doubleSumFileTest() throws Exception {
        int totalDoubleLen = 111238473;
        long byteLen = ((long)totalDoubleLen) << 3;
        int block_off_set = 22;
        long singleByteLen = 1L << block_off_set;
        int blocks =  (int)(byteLen>>block_off_set) + 1;
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Field fieldHead = FileConstants.class.getDeclaredField("HEAD");
        fieldHead.setAccessible(true);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, fieldHead.get(null));
        final java.util.zip.ZipFile file = new java.util.zip.ZipFile("D:/fine2.cube");
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            @Override
            public InputStream answer() throws Throwable {
                return file.getInputStream(file.getEntry("head"));
            }
        }).anyTimes();
        for(int i = 0; i < blocks; i++) {
            FileBlock block_i = constructor.newInstance(u, String.valueOf(i));
            final int index = i;
            EasyMock.expect(connector.read(EasyMock.eq(block_i))).andAnswer(new IAnswer<InputStream>() {
                @Override
                public InputStream answer() throws Throwable {
                    return file.getInputStream(file.getEntry(String.valueOf(index)));
                }
            }).anyTimes();
        }
        control.replay();
        ReadIOFile<DoubleReadBuffer> dfile = (ReadIOFile<DoubleReadBuffer>) FineIO.createIOFile(connector , u, FineIO.MODEL.READ_DOUBLE);
        double d1 = 0;
        long t = System.currentTimeMillis();
        for(long i = 0, ilen = totalDoubleLen; i < ilen; i++){
            d1 += ReadIOFile.getDouble(dfile, i);
        }
        System.out.println("first cost:"  + (System.currentTimeMillis() - t) + "ms value:" + d1);
        d1 = 0;
        t = System.currentTimeMillis();
        for(long i = 0, ilen = totalDoubleLen; i < ilen; i++){
            d1 += ReadIOFile.getDouble(dfile, i);
        }
        System.out.println("second cost:"  + (System.currentTimeMillis() - t) + "ms value:" + d1);
    }

    private static void createFile() {
        int totalDoubleLen = 111238473;
        long byteLen = ((long)totalDoubleLen) << 3;
        int block_off_set = 22;
        long singleByteLen = 1L << block_off_set;
        int blocks =  (int)(byteLen>>block_off_set) + 1;
        final byte[] head = new byte[16];
        Bits.putInt(head, 0, blocks);
        head[8] = (byte) block_off_set;
        writeFile("head", head);
        for(int i = 0; i < blocks; i++) {
            long len = singleByteLen;
            if(i == blocks - 1){
                len = byteLen & (singleByteLen - 1);
            }
            byte[] bytes = createRandomByte((int)len);
            writeFile(String.valueOf(i), bytes);
        }
    }

    private static void writeFile(String name, byte[] bytes) {
        File file = new File(name);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(file);
            fs.write(bytes);
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doubleSumTest() throws Exception {
        int totalDoubleLen = 111238473;
        long byteLen = ((long)totalDoubleLen) << 3;
        int block_off_set = 22;
        long singleByteLen = 1L << block_off_set;
        int blocks =  (int)(byteLen>>block_off_set) + 1;
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
            @Override
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(head);
            }
        }).anyTimes();
        for(int i = 0; i < blocks; i++) {
            long len = singleByteLen;
            if(i == blocks - 1){
                len = byteLen & (singleByteLen - 1);
            }
            final byte[] bytes = createRandomByte((int)len);
            FileBlock block_i = constructor.newInstance(u, String.valueOf(i));
            EasyMock.expect(connector.read(EasyMock.eq(block_i))).andAnswer(new IAnswer<InputStream>() {
                @Override
                public InputStream answer() throws Throwable {
                    return new ByteArrayInputStream(bytes);
                }
            }).anyTimes();
        }
        control.replay();
        ReadIOFile<DoubleReadBuffer> dfile = (ReadIOFile<DoubleReadBuffer>) FineIO.createIOFile(connector , u, FineIO.MODEL.READ_DOUBLE);
        double d1 = 0;
        long t = System.currentTimeMillis();
        for(long i = 0, ilen = totalDoubleLen; i < ilen; i++){
            d1 += ReadIOFile.getDouble(dfile, i);
        }
        System.out.println("first cost:"  + (System.currentTimeMillis() - t) + "ms value:" + d1);
        d1 = 0;
        t = System.currentTimeMillis();
        for(long i = 0, ilen = totalDoubleLen; i < ilen; i++){
            d1 += ReadIOFile.getDouble(dfile, i);
        }
        System.out.println("second cost:"  + (System.currentTimeMillis() - t) + "ms value:" + d1);
    }



    private static void doubleTest(byte[] bytes, Connector connector, FileBlock block) {
        long t = System.currentTimeMillis();
        DoubleReadBuffer db = createBuffer(DoubleReadBuffer.class, connector, block, 27);
        double d = 0;
        for(int k = 0, klen = (bytes.length >> 3); k < klen; k++){
            d +=db.get(k);
        }
        System.out.println( "first get double:"+(System.currentTimeMillis() - t) +"ms result："+ d);
        t = System.currentTimeMillis();
        d = 0;
        for(int k = 0, klen = (bytes.length >> 3); k < klen; k++){
            d +=db.get(k);
        }
        System.out.println( "second get double:"+(System.currentTimeMillis() - t)+"ms result："+ d);
    }

    private static void intTest(byte[] bytes, Connector connector, FileBlock block) {
        long t = System.currentTimeMillis();
        IntReadBuffer ib = createBuffer(IntReadBuffer.class, connector, block, 28);
        int v = 0;
        for(int k = 0, klen = (bytes.length >> 2); k < klen; k++){
            v +=ib.get(k);
        }
        System.out.println( "first get int:"+(System.currentTimeMillis() - t) +"ms result："+ v);
        t = System.currentTimeMillis();
        v = 0;
        for(int k = 0, klen = (bytes.length >> 2); k < klen; k++){
            v +=ib.get(k);
        }
        System.out.println( "second get int:"+(System.currentTimeMillis() - t)+"ms result："+ v);
        ib.clear();
    }

    private static void byteTest(byte[] bytes, Connector connector, FileBlock block) {
        ByteReadBuffer buffer = createBuffer(ByteReadBuffer.class, connector, block, 30);
        long t = System.currentTimeMillis();
        byte r = 0;
        for(int i = 0; i < bytes.length; i++){
            r += buffer.get(i);
        }
        System.out.println("first get byte:" +(System.currentTimeMillis() - t) +"ms result："+ r);
        t = System.currentTimeMillis();
        r = 0;
        for(int i = 0; i < bytes.length; i++){
            r += buffer.get(i);
        }
        System.out.println( "second get byte:"+(System.currentTimeMillis() - t) +"ms result："+ r);
        buffer.clear();
    }

    private  static  <T extends AbstractBuffer>  T createBuffer(Class<T> clazz, Object connector, Object block, int off) {
        try {
            Constructor<T>  constructor= clazz.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
            constructor.setAccessible(true);
            return  constructor.newInstance(connector, block, off);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
