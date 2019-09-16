package com.fineio.test.v2.io.file;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.AbstractConnector;
import com.fineio.storage.Connector;
import com.fineio.v2.FineIO;
import com.fineio.v2.io.ByteBuffer;
import com.fineio.v2.io.CharBuffer;
import com.fineio.v2.io.FloatBuffer;
import com.fineio.v2.io.IntBuffer;
import com.fineio.v2.io.LongBuffer;
import com.fineio.v2.io.ShortBuffer;
import com.fineio.v2.io.file.IOFileV2;
import com.fineio.v2.io.file.writer.JobFinishedManager;
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
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/6/1
 */
public class WriteIOFileV2Test extends TestCase {
    private static int ARRAY_LEN = 10000000;

    public void testEmptyFile() throws Exception {
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        byte size = 26;
        connector.getBlockOffset();
        EasyMock.expectLastCall().andReturn(size).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().anyTimes();
        control.replay();
        IOFileV2 file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_BYTE);
        file.close();
    }

    public void testConstruct() throws Exception {
        final byte size = 26;
        Connector connector = new MemoryConnector() {
            public byte getBlockOffset() {
                return size;
            }
        };
        URI u = new URI("");
        IOFileV2 file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_BYTE);
        Field field = IOFileV2.class.getDeclaredField("block_size_offset");
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

    public void testWriteDouble() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFileV2 file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_DOUBLE);
        double[] doubles = createRandomDouble();
        for (int i = 0; i < doubles.length; i++) {
            FineIO.put(file, doubles[i]);
        }
        file.close();
        final IOFileV2 read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_DOUBLE);
        for (int i = 0; i < doubles.length; i++) {
            assertEquals(doubles[i], FineIO.getDouble(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < doubles.length; i++) {
            v += FineIO.getDouble(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        checkMemory(read);
    }

    public void testWriteLong() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFileV2<LongBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_LONG);
        long[] values = createRandomLong();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        final IOFileV2 read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_LONG);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getLong(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getLong(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        checkMemory(read);
    }

    public void testWriteChar() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFileV2<CharBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_CHAR);
        char[] values = createRandomChar();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        final IOFileV2 read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_CHAR);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getChar(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getChar(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        checkMemory(read);
    }

    public void testWriteInt() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFileV2<IntBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_INT);
        int[] values = createRandomInt();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        final IOFileV2 read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_INT);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getInt(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getInt(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        checkMemory(read);
    }

    public void testWriteFloat() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFileV2<FloatBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_FLOAT);
        float[] values = createRandomFloat();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        final IOFileV2 read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_FLOAT);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getFloat(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getFloat(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        checkMemory(read);
    }

    public void testWriteShort() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFileV2<ShortBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_SHORT);
        short[] values = createRandomShort();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        final IOFileV2 read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_SHORT);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getShort(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getShort(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        checkMemory(read);
    }

    public void testWriteByte() throws Exception {
        Connector connector = new MemoryConnector();
        URI u = new URI("test");
        IOFileV2<ByteBuffer> file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_BYTE);
        byte[] values = createRandomByte();
        for (int i = 0; i < values.length; i++) {
            FineIO.put(file, values[i]);
        }
        file.close();
        final IOFileV2 read = FineIO.createIOFile(connector, u, FineIO.MODEL.READ_BYTE);
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], FineIO.getByte(read, i));
        }
        long t = System.currentTimeMillis();
        double v = 0;
        for (int i = 0; i < values.length; i++) {
            v += FineIO.getByte(read, i);
        }
        System.out.println((System.currentTimeMillis() - t) + " ms");
        checkMemory(read);

    }

    private void checkMemory(final IOFileV2 read) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] check = new boolean[]{true};
        JobFinishedManager.getInstance().finish(new Runnable() {
            @Override
            public void run() {
                read.close();
                try {
                    assertEquals(FineIO.getCurrentMemorySize(), 0);
                    assertEquals(FineIO.getCurrentReadMemorySize(), 0);
                    assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
                    assertEquals(FineIO.getReadWaitCount(), 0);
                    assertEquals(FineIO.getWriteWaitCount(), 0);
                } catch (Throwable e) {
                    check[0] = false;
                }
                latch.countDown();
            }
        });
        latch.await();
        assertTrue(check[0]);
    }

    public static class MemoryConnector extends AbstractConnector {

        private Map<FileBlock, byte[]> map = new ConcurrentHashMap<FileBlock, byte[]>();


        public InputStream read(FileBlock file) {
            byte[] b = map.get(file);
            if (b != null) {
                return new ByteArrayInputStream(b);
            }
            return null;
        }


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


        public boolean delete(FileBlock block) {
            map.remove(block);
            return true;
        }

        @Override
        public boolean exists(FileBlock block) {
            return false;
        }

        @Override
        public boolean copy(FileBlock srcBlock, FileBlock destBlock) {
            return false;
        }
    }
}