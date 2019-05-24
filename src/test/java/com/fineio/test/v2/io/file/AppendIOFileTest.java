package com.fineio.test.v2.io.file;

import com.fineio.FineIO;
import com.fineio.accessor.Block;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.IOFile;
import com.fineio.storage.AbstractConnector;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/6/1
 */
public class AppendIOFileTest extends TestCase {
    private static int ARRAY_LEN = 10000000;

    public void testConstruct() throws Exception {
        final byte size = 26;
//        Connector connector = new MemoryConnector() {
//            public byte getBlockOffset() {
//                return size;
//            }
//        };
        IMocksControl control = EasyMock.createControl();
        Connector mockConnector = control.createMock(Connector.class);
        EasyMock.expect(mockConnector.getBlockOffset()).andReturn(size).anyTimes();
        mockConnector.write(EasyMock.isA(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().anyTimes();
        control.replay();

        URI u = new URI("testConstruct");
        IOFile file = FineIO.createIOFile(mockConnector, u, FineIO.MODEL.APPEND_BYTE);
        Field field = IOFile.class.getDeclaredField("block_size_offset");
        field.setAccessible(true);
        assertEquals(size, ((Byte) field.get(file)).byteValue());
        file.close();
    }

    private double[] createRandomDouble() {
        int len = ARRAY_LEN;
        double[] arrays = new double[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = Math.random() * 100000000000d;
        }
        return arrays;
    }

    private float[] createRandomFloat() {
        int len = ARRAY_LEN;
        float[] arrays = new float[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (float) (Math.random() * 100000000000d);
        }
        return arrays;
    }


    private long[] createRandomLong() {
        int len = ARRAY_LEN;
        long[] arrays = new long[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }


    private int[] createRandomInt() {
        int len = ARRAY_LEN;
        int[] arrays = new int[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (int) Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    private short[] createRandomShort() {
        int len = ARRAY_LEN;
        short[] arrays = new short[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (short) Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    private char[] createRandomChar() {
        int len = ARRAY_LEN;
        char[] arrays = new char[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (char) Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    private byte[] createRandomByte() {
        int len = ARRAY_LEN;
        byte[] arrays = new byte[len];
        for (int i = 0; i < len; i++) {
            arrays[i] = (byte) Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    public void testAppendDouble() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFile file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_DOUBLE);
        double[] doubles = createRandomDouble();
        for (int i = 0; i < doubles.length; i++) {
            FineIO.put(file, doubles[i]);
        }
        file.close();
        IOFile read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_DOUBLE);
        for (int i = 0; i < doubles.length; i++) {
            assertEquals(doubles[i], FineIO.getDouble(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < doubles.length; i++) {
            v += FineIO.getDouble(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        read.close();
        IOFile edit = FineIO.createIOFile(connector, u, FineIO.MODEL.APPEND_DOUBLE);
        int change = doubles.length / 2;
        double beforeChange = doubles[change];
        doubles[change] = -100000000 * Math.random();
        FineIO.put(edit, change, doubles[change]);
        int position = doubles.length;
        edit.close();
        read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_DOUBLE);
        assertEquals(FineIO.getDouble(read, change), beforeChange);
        assertEquals(FineIO.getDouble(read, position), doubles[change]);

        assertTrue(FineIO.getCurrentMemorySize() > 0);
        assertTrue(FineIO.getCurrentReadMemorySize() > 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        read.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testAppendLong() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFile<LongBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_LONG);
        long[] values = createRandomLong();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        IOFile read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_LONG);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getLong(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getLong(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        read.close();
        IOFile<LongBuffer> edit = FineIO.createIOFile(connector, u, FineIO.MODEL.APPEND_LONG);
        int change = values.length / 2;
        long lastValue = values[change];
        values[change] = Double.doubleToLongBits(-100000000 * Math.random());
        FineIO.put(edit, change, values[change]);
        int position = values.length;
        edit.close();
        read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_LONG);
        assertEquals(FineIO.getLong(read, change), lastValue);
        assertEquals(FineIO.getLong(read, position), values[change]);
        assertTrue(FineIO.getCurrentMemorySize() > 0);
        assertTrue(FineIO.getCurrentReadMemorySize() > 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        read.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testAppendChar() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFile<CharBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_CHAR);
        char[] values = createRandomChar();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        IOFile read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_CHAR);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getChar(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getChar(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        read.close();
        IOFile<CharBuffer> edit = FineIO.createIOFile(connector, u, FineIO.MODEL.APPEND_CHAR);
        int change = values.length / 2;
        char lastValue = values[change];
        values[change] = (char) Double.doubleToLongBits(-100000000 * Math.random());
        FineIO.put(edit, change, values[change]);
        int position = values.length;
        edit.close();
        read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_CHAR);
        assertEquals(FineIO.getChar(read, change), lastValue);
        assertEquals(FineIO.getChar(read, position), values[change]);
        assertTrue(FineIO.getCurrentMemorySize() > 0);
        assertTrue(FineIO.getCurrentReadMemorySize() > 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        read.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testAppendInt() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFile<IntBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_INT);
        int[] values = createRandomInt();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        IOFile read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_INT);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getInt(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getInt(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        read.close();
        IOFile<IntBuffer> edit = FineIO.createIOFile(connector, u, FineIO.MODEL.APPEND_INT);
        int change = values.length / 2;
        int lastValue = values[change];
        values[change] = (int) Double.doubleToLongBits(-100000000 * Math.random());
        boolean exp = false;
        FineIO.put(edit, change, values[change]);
        int position = values.length;
        edit.close();
        read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_INT);
        assertEquals(FineIO.getInt(read, change), lastValue);
        assertEquals(FineIO.getInt(read, position), values[change]);
        assertTrue(FineIO.getCurrentMemorySize() > 0);
        assertTrue(FineIO.getCurrentReadMemorySize() > 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        read.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testAppendFloat() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFile<FloatBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_FLOAT);
        float[] values = createRandomFloat();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        IOFile read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_FLOAT);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getFloat(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getFloat(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        read.close();
        IOFile<FloatBuffer> edit = FineIO.createIOFile(connector, u, FineIO.MODEL.APPEND_FLOAT);
        int change = values.length / 2;
        float lastValue = values[change];
        values[change] = (float) Double.doubleToLongBits(-100000000 * Math.random());
        FineIO.put(edit, change, values[change]);
        int position = values.length;
        edit.close();
        read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_FLOAT);
        assertEquals(FineIO.getFloat(read, change), lastValue);
        assertEquals(FineIO.getFloat(read, position), values[change]);
        assertTrue(FineIO.getCurrentMemorySize() > 0);
        assertTrue(FineIO.getCurrentReadMemorySize() > 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        read.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testAppendShort() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFile<ShortBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_SHORT);
        short[] values = createRandomShort();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        IOFile read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_SHORT);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getShort(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getShort(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        read.close();
        IOFile<ShortBuffer> edit = FineIO.createIOFile(connector, u, FineIO.MODEL.APPEND_SHORT);
        int change = values.length / 2;
        short lastValue = values[change];
        values[change] = (short) Double.doubleToLongBits(-100000000 * Math.random());
        FineIO.put(edit, change, values[change]);
        int position = values.length;
        edit.close();
        read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_SHORT);
        assertEquals(FineIO.getShort(read, change), lastValue);
        assertEquals(FineIO.getShort(read, position), values[change]);
        assertTrue(FineIO.getCurrentMemorySize() > 0);
        assertTrue(FineIO.getCurrentReadMemorySize() > 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        read.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public void testAppendByte() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFile<ByteBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_BYTE);
        byte[] values = createRandomByte();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        IOFile read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_BYTE);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getByte(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getByte(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        read.close();
        IOFile<ByteBuffer> edit = FineIO.createIOFile(connector, u, FineIO.MODEL.APPEND_BYTE);
        int change = values.length / 2;
        byte lastValue = values[change];
        values[change] = (byte) Double.doubleToLongBits(-100000000 * Math.random());
        boolean exp = false;
        FineIO.put(edit, change, values[change]);
        int position = values.length;
        edit.close();
        read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_BYTE);
        assertEquals(FineIO.getByte(read, change), lastValue);
        assertEquals(FineIO.getByte(read, position), values[change]);
        assertTrue(FineIO.getCurrentMemorySize() > 0);
        assertTrue(FineIO.getCurrentReadMemorySize() > 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        read.close();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
    }

    public static class MemoryConnector extends AbstractConnector {

        private Map<FileBlock, byte[]> map = new ConcurrentHashMap<FileBlock, byte[]>();

        @Override
        public InputStream read(FileBlock file) {
            byte[] b = map.get(file);
            if (b != null) {
                return new ByteArrayInputStream(b);
            }
            return null;
        }

        @Override
        public void write(FileBlock file, InputStream inputStream) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] temp = new byte[1024];
            int len = 0;
            try {
                while ((len = inputStream.read(temp)) > 0) {
                    byteArrayOutputStream.write(temp, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            map.put(file, byteArrayOutputStream.toByteArray());
        }

        @Override
        public boolean delete(FileBlock block) {
            map.remove(block);
            return true;
        }

        @Override
        public boolean exists(FileBlock block) {
            return false;
        }

        @Override
        public boolean delete(Block block) {
            return false;
        }

        @Override
        public boolean exists(Block block) {
            return false;
        }

        @Override
        public Block list(String dir) {
            return null;
        }

        @Override
        public boolean copy(FileBlock srcBlock, FileBlock destBlock) {
            return false;
        }
    }
}