package com.fineio.test.v2;

import com.fineio.base.Bits;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileConstants;
import com.fineio.storage.Connector;
import com.fineio.test.v2.io.file.WriteIOFileV2Test;
import com.fineio.v2.FineIO;
import com.fineio.v2.io.DoubleBuffer;
import com.fineio.v2.io.IntBuffer;
import com.fineio.v2.io.LongBuffer;
import com.fineio.v2.io.file.IOFileV2;
import com.fineio.v2.io.file.ReadIOFileV2;
import com.fineio.v2.io.file.WriteIOFileV2;
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

/**
 * @author yee
 * @date 2018/6/1
 */
public class FineIOTest extends TestCase {

    public void testCreateReadIOFilePlus() throws Exception {

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
            public InputStream answer() {
                return new ByteArrayInputStream(res);
            }
        }).anyTimes();
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte) 22);
        control.replay();
        IOFileV2 file = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_LONG);
        assertTrue(file instanceof ReadIOFileV2);
        file.close();
    }

    public void testCreateWriteFineIOPlus() throws Exception {
        Connector connector = new WriteIOFileV2Test.MemoryConnector();
        URI u = new URI("");
        IOFileV2<DoubleBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_DOUBLE);
        assertTrue(file instanceof WriteIOFileV2);
        file.close();
    }


    private byte[] createRandomByte(int len) {
        byte[] arrays = new byte[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (byte) (Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }


    protected Connector getConnector(byte[] block0, byte[] block1, byte[] block2, byte[] block3, byte[] head, URI u) throws NoSuchFieldException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, IOException {
        Connector connector = new WriteIOFileV2Test.MemoryConnector();
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


    public void testRead() throws Exception {
        int blocks = 4;
        int block_off_set = 23;
        int byteLen = (1 << block_off_set);
        final byte[] block0 = createRandomByte(byteLen);
        final byte[] block1 = createRandomByte(byteLen);
        final byte[] block2 = createRandomByte(byteLen);
        final byte[] block3 = createRandomByte(byteLen >> 3);
        long totalLen = (((long) byteLen) * 3 + block3.length);
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
            public InputStream answer() {
                return new ByteArrayInputStream(head);
            }
        }).anyTimes();
        FileBlock block_0 = constructor.newInstance(u, String.valueOf(0));
        EasyMock.expect(connector.read(EasyMock.eq(block_0))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() {
                return new ByteArrayInputStream(block0);
            }
        }).anyTimes();
        FileBlock block_1 = constructor.newInstance(u, String.valueOf(1));
        EasyMock.expect(connector.read(EasyMock.eq(block_1))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() {
                return new ByteArrayInputStream(block1);
            }
        }).anyTimes();
        FileBlock block_2 = constructor.newInstance(u, String.valueOf(2));
        EasyMock.expect(connector.read(EasyMock.eq(block_2))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() {
                return new ByteArrayInputStream(block2);
            }
        }).anyTimes();
        FileBlock block_3 = constructor.newInstance(u, String.valueOf(3));
        EasyMock.expect(connector.read(EasyMock.eq(block_3))).andAnswer(new IAnswer<InputStream>() {
            public InputStream answer() {
                return new ByteArrayInputStream(block3);
            }
        }).anyTimes();
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte) 22).anyTimes();
        control.replay();
        u = new URI("");
        ReadIOFileV2<LongBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_LONG);
        long v1 = 0;
        for (long i = 0, ilen = (totalLen >> 3); i < ilen; i++) {
            v1 += FineIO.getLong(file, i);
        }
        long v2 = 0;
        for (int i = 0; i < byteLen; i += 8) {
            v2 += Bits.getLong(block0, i);
            v2 += Bits.getLong(block1, i);
            v2 += Bits.getLong(block2, i);
            if (i < block3.length) {
                v2 += Bits.getLong(block3, i);
            }
        }
        assertEquals(v1, v2);
        ReadIOFileV2<IntBuffer> ifile = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_INT);
        v1 = 0;
        for (long i = 0, ilen = (totalLen >> 2); i < ilen; i++) {
            v1 += FineIO.getInt(ifile, i);
        }
        v2 = 0;
        for (int i = 0; i < byteLen; i += 4) {
            v2 += Bits.getInt(block0, i);
            v2 += Bits.getInt(block1, i);
            v2 += Bits.getInt(block2, i);
            if (i < block3.length) {
                v2 += Bits.getInt(block3, i);
            }
        }
        assertEquals(v1, v2);
        ReadIOFileV2<DoubleBuffer> dfile = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_DOUBLE);
        double d1 = 0;
        for (long i = 0, ilen = (totalLen >> 3); i < ilen; i++) {
            d1 += FineIO.getDouble(dfile, i);
        }
        double d2 = 0;
        for (int i = 0; i < byteLen; i += 8) {
            d2 += Bits.getDouble(block0, i);
            d2 += Bits.getDouble(block1, i);
            d2 += Bits.getDouble(block2, i);
            if (i < block3.length) {
                d2 += Bits.getDouble(block3, i);
            }
        }
        assertEquals(d1, d2);
        assertTrue(FineIO.getCurrentMemorySize() > 0);
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

    private double[] createRandomDouble(int len) {
        double[] arrays = new double[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (Math.random() * 100000000000d);
        }
        return arrays;
    }

    public void testWrite() throws Exception {
        URI u = new URI("testWrite");
        Connector connector = new WriteIOFileV2Test.MemoryConnector();
        WriteIOFileV2<DoubleBuffer> dfile = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_DOUBLE);
        int len = 1000000;
        double[] doubles = createRandomDouble(len);
        for (int i = 0; i < doubles.length; i++) {
            FineIO.put(dfile, i, doubles[i]);
        }

        assertTrue(FineIO.getCurrentMemorySize() > 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertTrue(FineIO.getCurrentWriteMemorySize() > 0);
        dfile.close();
        assertTrue(FineIO.getCurrentMemorySize() > 0);
        assertTrue(FineIO.getCurrentReadMemorySize() > 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);

        ReadIOFileV2<DoubleBuffer> readDfile = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_DOUBLE);
        for (int i = 0; i < doubles.length; i++) {
            assertEquals(FineIO.getDouble(readDfile, i), doubles[i]);
        }
        readDfile.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
    }


    public void testDirectAccess() {
//        Connector connector = new WriteIOFileV2Test.MemoryConnector();
//        URI uri = URI.create("A");
//        DirectWriteIOFile<DoubleBuffer> dw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_DOUBLE);
//        int len = 1000000;
//        double[] doubles = createRandomDouble(len);
//        for (int i = 0; i < len; i++) {
//            FineIO.put(dw, i, doubles[i]);
//        }
//        dw.close();
//        DirectReadIOFile<DoubleBuffer> dr = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_DOUBLE);
//        for (int i = len; i > 0; i--) {
//            assertEquals(doubles[i - 1], FineIO.getDouble(dr, i - 1));
//        }
//        dr.close();
//
//
//        DirectWriteIOFile<CharBuffer> cw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_CHAR);
//        for (int i = 0; i < len; i++) {
//            FineIO.put(cw, i, (char) doubles[i]);
//        }
//        cw.close();
//        DirectReadIOFile<CharBuffer> cr = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_CHAR);
//        for (int i = len; i > 0; i--) {
//            assertEquals((char) doubles[i - 1], FineIO.getChar(cr, i - 1));
//        }
//        cr.close();
//
//        DirectWriteIOFile<LongBuffer> lw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_LONG);
//        for (int i = 0; i < len; i++) {
//            FineIO.put(lw, i, (long) doubles[i]);
//        }
//        lw.close();
//        DirectReadIOFile<LongBuffer> lr = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_LONG);
//        for (int i = len; i > 0; i--) {
//            assertEquals((long) doubles[i - 1], FineIO.getLong(lr, i - 1));
//        }
//        lr.close();
//
//
//        DirectWriteIOFile<IntBuffer> iw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_INT);
//        for (int i = 0; i < len; i++) {
//            FineIO.put(iw, i, (int) doubles[i]);
//        }
//        iw.close();
//        DirectReadIOFile<IntBuffer> ir = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_INT);
//        for (int i = len; i > 0; i--) {
//            assertEquals((int) doubles[i - 1], FineIO.getInt(ir, i - 1));
//        }
//        ir.close();
//
//        DirectWriteIOFile<ByteBuffer> bw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_BYTE);
//        for (int i = 0; i < len; i++) {
//            FineIO.put(bw, i, (byte) doubles[i]);
//        }
//        bw.close();
//        DirectReadIOFile<ByteBuffer> br = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_BYTE);
//        for (int i = len; i > 0; i--) {
//            assertEquals((byte) doubles[i - 1], FineIO.getByte(br, i - 1));
//        }
//        br.close();
//
//        DirectWriteIOFile<ShortBuffer> sw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_SHORT);
//        for (int i = 0; i < len; i++) {
//            FineIO.put(sw, i, (short) doubles[i]);
//        }
//        sw.close();
//        DirectReadIOFile<ShortBuffer> sr = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_SHORT);
//        for (int i = len; i > 0; i--) {
//            assertEquals((short) doubles[i - 1], FineIO.getShort(sr, i - 1));
//        }
//        sr.close();
//
//        DirectWriteIOFile<FloatBuffer> fw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_FLOAT);
//        for (int i = 0; i < len; i++) {
//            FineIO.put(fw, i, (float) doubles[i]);
//        }
//        fw.close();
//        DirectReadIOFile<FloatBuffer> fr = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_FLOAT);
//        for (int i = len; i > 0; i--) {
//            assertEquals((float) doubles[i - 1], FineIO.getFloat(fr, i - 1));
//        }
//        fr.close();
    }
}