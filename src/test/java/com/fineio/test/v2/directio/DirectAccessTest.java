package com.fineio.test.v2.directio;

import com.fineio.FineIO;
import com.fineio.directio.DirectIOFile;
import com.fineio.io.IntBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.test.v2.io.file.WriteIOFileTest;
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
            files[i] = FineIO.createIOFile(connector, URI.create(String.valueOf(i)), FineIO.MODEL.DIRECT_READ_BYTE);
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

        for (int i = 0; i < len; i++) {
            DirectIOFile<IntBuffer> file = FineIO.createIOFile(connector, URI.create(String.valueOf(i)), FineIO.MODEL.DIRECT_WRITE_INT);
            for (int k = 0; k < i; k++) {
                FineIO.put(file, k, i);
            }
            file.close();
        }
        for (int i = 0; i < len; i++) {
            DirectIOFile<IntBuffer> file = FineIO.createIOFile(connector, URI.create(String.valueOf(i)), FineIO.MODEL.DIRECT_READ_INT);
            assertEquals(file.length(), i);
            for (int k = 0; k < i; k++) {
                assertEquals(i, FineIO.getInt(file, k));
            }
            file.close();
        }
    }
}
