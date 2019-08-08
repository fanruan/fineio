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

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2019/4/18
 */
public class AppendFileDemo {
    private FileBlock key = new FileBlock(System.getProperty("user.dir"), "append");
    private FileConnector connector = new FileConnector((byte) 10);
    private FileConnector newConnector = new FileConnector((byte) 5);
    private int n = 1 << 10;

    @Before
    public void setUp() throws Exception {
        File[] children = new File(key.getPath()).listFiles();
        if (children != null) {
            for (File child : children) {
                child.delete();
            }
        }

        BufferCache.get().invalidateAll();
    }

    @Test
    public void testByte() {
        ByteAppendFile byteFile = new ByteAppendFile(ByteWriteFile.ofSync(key, connector));
        for (int i = -128; i < 0; i++) {
            byteFile.putByte((byte) i);
        }
        byteFile.close();

        byteFile = new ByteAppendFile(ByteWriteFile.ofSync(key, newConnector));
        for (int i = 0; i < 128; i++) {
            byteFile.putByte((byte) i);
        }
        byteFile.close();

        ByteReadFile byteReadFile = new ByteReadFile(key, connector);
        for (int i = 0; i < 256; i++) {
            assertEquals(i - 128, byteReadFile.getByte(i));
        }
    }

    @Test
    public void testInt() {
        IntAppendFile intFile = new IntAppendFile(IntWriteFile.ofSync(key, connector));
        for (int i = 0; i < n >> 1; i++) {
            intFile.putInt(i);
        }
        intFile.close();

        intFile = new IntAppendFile(IntWriteFile.ofSync(key, newConnector));
        for (int i = n >> 1; i < n; i++) {
            intFile.putInt(i);
        }
        intFile.close();

        IntReadFile intReadFile = new IntReadFile(key, connector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, intReadFile.getInt(i));
        }
    }

    @Test
    public void testLong() {
        LongAppendFile longAppendFile = new LongAppendFile(LongWriteFile.ofSync(key, connector));
        for (int i = 0; i < n >> 1; i++) {
            longAppendFile.putLong(i);
        }
        longAppendFile.close();

        longAppendFile = new LongAppendFile(LongWriteFile.ofSync(key, newConnector));
        for (int i = n >> 1; i < n; i++) {
            longAppendFile.putLong(i);
        }
        longAppendFile.close();

        LongReadFile longReadFile = new LongReadFile(key, connector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, longReadFile.getLong(i));
        }
    }

    @Test
    public void testDouble() {
        DoubleAppendFile doubleAppendFile = new DoubleAppendFile(DoubleWriteFile.ofSync(key, connector));
        for (int i = 0; i < n >> 1; i++) {
            doubleAppendFile.putDouble(i);
        }
        doubleAppendFile.close();

        doubleAppendFile = new DoubleAppendFile(DoubleWriteFile.ofSync(key, newConnector));
        for (int i = n >> 1; i < n; i++) {
            doubleAppendFile.putDouble(i);
        }
        doubleAppendFile.close();

        DoubleReadFile doubleReadFile = new DoubleReadFile(key, connector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, doubleReadFile.getDouble(i), 0);
        }
    }
}