package com.fineio.test.directio;

import com.fineio.FineIO;
import com.fineio.directio.DirectIOFile;
import com.fineio.io.file.FileBlock;
import com.fineio.test.file.FineWriteIOTest;
import com.fineio.test.io.MemoryLeakTest;
import junit.framework.TestCase;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * Created by daniel on 2017/4/25.
 */
public class DirectAccessTest extends TestCase {


    public void testDirectAccess() throws IOException {
        FineWriteIOTest.MemoryConnector connector = new FineWriteIOTest.MemoryConnector();
        int len = 10000;
        long t = System.currentTimeMillis();
        for(int i = 0; i < len; i++) {
            byte[] b = new byte[i];
            Arrays.fill(b, (byte)i);
            connector.write(new FileBlock(URI.create(String.valueOf(i))), b);
        }
        DirectIOFile[] files = new DirectIOFile[len];
        for(int i = 0; i < len; i++) {
            files [i] = FineIO.createIOFile(connector, URI.create(String.valueOf(i)), FineIO.MODEL.READ_BYTE_DIRECT);
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for(int i = 0; i < len; i++) {
            assertEquals(i, files [i].length());
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for(int i = 0; i < len; i++) {
            assertEquals(i, files [i].length());
            for( int k = 0; k < i; k++) {
                assertEquals((byte)i, FineIO.getByte(files [i], k));
            }
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for(int i = 0; i < len; i++) {
            assertEquals(i, files [i].length());
            for( int k = 0; k < i; k++) {
                assertEquals((byte)i, FineIO.getByte(files [i], k));
            }
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for(int i = 0; i < len; i++) {
            files [i].close();
        }
        System.out.println(System.currentTimeMillis() - t);
        MemoryLeakTest.assertZeroMemory();

    }
}
