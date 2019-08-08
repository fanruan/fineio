package com.fineio.test.v2;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.directio.DirectReadIOFile;
import com.fineio.directio.DirectWriteIOFile;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileConstants;
import com.fineio.io.file.IOFile;
import com.fineio.io.file.ReadIOFile;
import com.fineio.io.file.WriteIOFile;
import com.fineio.storage.Connector;
import com.fineio.test.v2.io.file.WriteIOFileTest;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.io.ByteArrayInputStream;
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
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u.getPath(), head.get(null));
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(() -> new ByteArrayInputStream(res)).anyTimes();
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte) 22);
        control.replay();
        IOFile file = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_LONG);
        assertTrue(file instanceof ReadIOFile);
        file.close();
    }

    public void testCreateWriteFineIOPlus() throws Exception {
        Connector connector = new WriteIOFileTest.MemoryConnector();
        URI u = new URI("");
        IOFile<DoubleBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_DOUBLE);
        assertTrue(file instanceof WriteIOFile);
        file.close();
    }


    private byte[] createRandomByte(int len) {
        byte[] arrays = new byte[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (byte) (Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
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
        String u = "/";
        Field fieldHead = FileConstants.class.getDeclaredField("HEAD");
        fieldHead.setAccessible(true);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, fieldHead.get(null));
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(() -> new ByteArrayInputStream(head)).anyTimes();
        FileBlock block_0 = constructor.newInstance(u, String.valueOf(0));
        EasyMock.expect(connector.read(EasyMock.eq(block_0))).andAnswer(() -> new ByteArrayInputStream(block0)).anyTimes();
        FileBlock block_1 = constructor.newInstance(u, String.valueOf(1));
        EasyMock.expect(connector.read(EasyMock.eq(block_1))).andAnswer(() -> new ByteArrayInputStream(block1)).anyTimes();
        FileBlock block_2 = constructor.newInstance(u, String.valueOf(2));
        EasyMock.expect(connector.read(EasyMock.eq(block_2))).andAnswer(() -> new ByteArrayInputStream(block2)).anyTimes();
        FileBlock block_3 = constructor.newInstance(u, String.valueOf(3));
        EasyMock.expect(connector.read(EasyMock.eq(block_3))).andAnswer(() -> new ByteArrayInputStream(block3)).anyTimes();
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte) 22).anyTimes();
        control.replay();
        u = "";
        ReadIOFile<LongBuffer> file = FineIO.createIOFile(connector, URI.create(u), FineIO.MODEL.READ_LONG);
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
        ReadIOFile<IntBuffer> ifile = FineIO.createIOFile(connector, URI.create(u), FineIO.MODEL.READ_INT);
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
        ReadIOFile<DoubleBuffer> dfile = FineIO.createIOFile(connector, URI.create(u), FineIO.MODEL.READ_DOUBLE);
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
        Connector connector = new WriteIOFileTest.MemoryConnector();
        WriteIOFile<DoubleBuffer> dfile = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_DOUBLE);
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

        ReadIOFile<DoubleBuffer> readDfile = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_DOUBLE);
        for (int i = 0; i < doubles.length; i++) {
            assertEquals(FineIO.getDouble(readDfile, i), doubles[i]);
        }
        readDfile.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
    }


    public void testDirectAccess() {
        Connector connector = new WriteIOFileTest.MemoryConnector();
        URI uri = URI.create("A");
        DirectWriteIOFile<DoubleBuffer> dw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_DOUBLE);
        int len = 1000000;
        double[] doubles = createRandomDouble(len);
        for (int i = 0; i < len; i++) {
            FineIO.put(dw, i, doubles[i]);
        }
        dw.close();
        DirectReadIOFile<DoubleBuffer> dr = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_DOUBLE);
        for (int i = len; i > 0; i--) {
            assertEquals(doubles[i - 1], FineIO.getDouble(dr, i - 1));
        }
        dr.close();


        DirectWriteIOFile<CharBuffer> cw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_CHAR);
        for (int i = 0; i < len; i++) {
            FineIO.put(cw, i, (char) doubles[i]);
        }
        cw.close();
        DirectReadIOFile<CharBuffer> cr = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_CHAR);
        for (int i = len; i > 0; i--) {
            assertEquals((char) doubles[i - 1], FineIO.getChar(cr, i - 1));
        }
        cr.close();

        DirectWriteIOFile<LongBuffer> lw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_LONG);
        for (int i = 0; i < len; i++) {
            FineIO.put(lw, i, (long) doubles[i]);
        }
        lw.close();
        DirectReadIOFile<LongBuffer> lr = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_LONG);
        for (int i = len; i > 0; i--) {
            assertEquals((long) doubles[i - 1], FineIO.getLong(lr, i - 1));
        }
        lr.close();


        DirectWriteIOFile<IntBuffer> iw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_INT);
        for (int i = 0; i < len; i++) {
            FineIO.put(iw, i, (int) doubles[i]);
        }
        iw.close();
        DirectReadIOFile<IntBuffer> ir = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_INT);
        for (int i = len; i > 0; i--) {
            assertEquals((int) doubles[i - 1], FineIO.getInt(ir, i - 1));
        }
        ir.close();

        DirectWriteIOFile<ByteBuffer> bw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_BYTE);
        for (int i = 0; i < len; i++) {
            FineIO.put(bw, i, (byte) doubles[i]);
        }
        bw.close();
        DirectReadIOFile<ByteBuffer> br = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_BYTE);
        for (int i = len; i > 0; i--) {
            assertEquals((byte) doubles[i - 1], FineIO.getByte(br, i - 1));
        }
        br.close();

        DirectWriteIOFile<ShortBuffer> sw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_SHORT);
        for (int i = 0; i < len; i++) {
            FineIO.put(sw, i, (short) doubles[i]);
        }
        sw.close();
        DirectReadIOFile<ShortBuffer> sr = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_SHORT);
        for (int i = len; i > 0; i--) {
            assertEquals((short) doubles[i - 1], FineIO.getShort(sr, i - 1));
        }
        sr.close();

        DirectWriteIOFile<FloatBuffer> fw = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_WRITE_FLOAT);
        for (int i = 0; i < len; i++) {
            FineIO.put(fw, i, (float) doubles[i]);
        }
        fw.close();
        DirectReadIOFile<FloatBuffer> fr = FineIO.createIOFile(connector, uri, FineIO.MODEL.DIRECT_READ_FLOAT);
        for (int i = len; i > 0; i--) {
            assertEquals((float) doubles[i - 1], FineIO.getFloat(fr, i - 1));
        }
        fr.close();
    }
}