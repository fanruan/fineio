package com.fineio.v3.file.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.v3.connector.FileConnector;
import com.fineio.v3.file.impl.read.ByteReadFile;
import com.fineio.v3.file.impl.read.DoubleReadFile;
import com.fineio.v3.file.impl.read.IntReadFile;
import com.fineio.v3.file.impl.read.LongReadFile;
import com.fineio.v3.file.impl.write.ByteWriteFile;
import com.fineio.v3.file.impl.write.DoubleWriteFile;
import com.fineio.v3.file.impl.write.IntWriteFile;
import com.fineio.v3.file.impl.write.LongWriteFile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2019/4/16
 */
public class FileDemo {
    private FileBlock key = new FileBlock(System.getProperty("user.dir"), "overwrite");
    private FileConnector connector = new FileConnector((byte) 10);
    private FileConnector newConnector = new FileConnector((byte) 5);
    private int n = 1 << 10;

    @Before
    public void setUp() {
        BufferCache.get().invalidateAll();
    }

    @Test
    public void testByte() {
        ByteWriteFile byteWriteFile = ByteWriteFile.ofSync(key, connector);
        for (int i = 0; i < 256; i++) {
            byteWriteFile.putByte(i, (byte) (i - 128));
        }
        byteWriteFile.close();

        ByteReadFile byteReadFile = new ByteReadFile(key, newConnector);
        for (int i = 0; i < 256; i++) {
            assertEquals(i - 128, byteReadFile.getByte(i));
        }
    }

    @Test
    public void testInt() {
        IntWriteFile intWriteFile = IntWriteFile.ofSync(key, connector);
        for (int i = 0; i < n; i++) {
            intWriteFile.putInt(i, i);
        }
        intWriteFile.close();

        IntReadFile intReadFile = new IntReadFile(key, newConnector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, intReadFile.getInt(i));
        }
    }

    @Test
    public void testLong() {
        LongWriteFile longWriteFile = LongWriteFile.ofSync(key, connector);
        for (int i = 0; i < n; i++) {
            longWriteFile.putLong(i, i);
        }
        longWriteFile.close();

        LongReadFile longReadFile = new LongReadFile(key, newConnector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, longReadFile.getLong(i));
        }
    }

    @Test
    public void testDouble() {
        DoubleWriteFile doubleWriteFile = DoubleWriteFile.ofSync(key, connector);
        for (int i = 0; i < n; i++) {
            doubleWriteFile.putDouble(i, i);
        }
        doubleWriteFile.close();

        DoubleReadFile doubleReadFile = new DoubleReadFile(key, newConnector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, doubleReadFile.getDouble(i), 0);
        }
    }
}