package com.fineio.test.v2.directio;

import com.fineio.io.file.FileBlock;
import com.fineio.test.v2.io.MemoryLeakTest;
import com.fineio.test.v2.io.file.WriteIOFileTest;
import com.fineio.v2.FineIO;
import com.fineio.v2.directio.DirectIOFile;
import com.fineio.v2.io.IntBuffer;
import junit.framework.TestCase;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * Created by daniel on 2017/4/25.
 */
public class DirectAccessTest extends TestCase {


    public void testDirectAccess() throws IOException {
        WriteIOFileTest.MemoryConnector connector = new WriteIOFileTest.MemoryConnector();
        int len = 10000;
        long t = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            byte[] b = new byte[i];
            Arrays.fill(b, (byte) i);
            connector.write(new FileBlock(URI.create(String.valueOf(i))), b);
        }
        DirectIOFile[] files = new DirectIOFile[len];
        for (int i = 0; i < len; i++) {
            files[i] = FineIO.createIOFile(connector, URI.create(String.valueOf(i)), FineIO.MODEL.READ_BYTE_DIRECT);
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            assertEquals(i, files[i].length());
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            assertEquals(i, files[i].length());
            for (int k = 0; k < i; k++) {
                assertEquals((byte) i, FineIO.getByte(files[i], k));
            }
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            assertEquals(i, files[i].length());
            for (int k = 0; k < i; k++) {
                assertEquals((byte) i, FineIO.getByte(files[i], k));
            }
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            files[i].close();
        }
        System.out.println(System.currentTimeMillis() - t);
        MemoryLeakTest.assertSizeMemory(0);

        for (int i = 0; i < len; i++) {
            DirectIOFile<IntBuffer> file = FineIO.createIOFile(connector, URI.create(String.valueOf(i)), FineIO.MODEL.WRITE_INT_DIRECT);
            for (int k = 0; k < i; k++) {
                FineIO.put(file, k, i);
            }
            file.close();
        }
        long size = 0;
        for (int i = 0; i < len; i++) {
            DirectIOFile<IntBuffer> file = FineIO.createIOFile(connector, URI.create(String.valueOf(i)), FineIO.MODEL.READ_INT_DIRECT);
            assertEquals(file.length(), i);
            for (int k = 0; k < i; k++) {
                assertEquals(i, FineIO.getInt(file, k));
            }
            file.close();
        }
        MemoryLeakTest.assertSizeMemory(0);
        size = 0;
        for (int i = 0; i < len; i++) {
            DirectIOFile<IntBuffer> file = FineIO.createIOFile(connector, URI.create(String.valueOf(i)), FineIO.MODEL.EDIT_INT_DIRECT);
            for (int k = 0; k < i; k++) {
                FineIO.put(file, k, FineIO.getInt(file, k) * 2);
            }

            file.close();
        }
        MemoryLeakTest.assertSizeMemory(0);
        for (int i = 0; i < len; i++) {
            DirectIOFile<IntBuffer> file = FineIO.createIOFile(connector, URI.create(String.valueOf(i)), FineIO.MODEL.READ_INT_DIRECT);
            assertEquals(file.length(), i);
            for (int k = 0; k < i; k++) {
                assertEquals(i * 2, FineIO.getInt(file, k));
            }
            file.close();
        }
        MemoryLeakTest.assertSizeMemory(0);
    }
}
