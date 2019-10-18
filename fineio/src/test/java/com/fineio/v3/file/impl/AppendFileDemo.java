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
    private FileConnector connector = new FileConnector((byte) 1);
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
        ByteWriteFile byteFile = ByteWriteFile.ofSync(key, connector);
        for (int i = 0; i < 128; i++) {
            byteFile.putByte(i, (byte) i);
        }
        byteFile.close();

        byteFile = ByteWriteFile.ofSync(key, newConnector);
        for (int i = 128; i < 256; i++) {
            byteFile.putByte(i, (byte) i);
        }
        byteFile.close();

        ByteReadFile byteReadFile = new ByteReadFile(key, connector);
        for (int i = 0; i < 256; i++) {
            assertEquals(i, byteReadFile.getByte(i) & 0xFF);
        }
    }

    @Test
    public void testInt() {
        IntWriteFile intFile = IntWriteFile.ofSync(key, connector);
        for (int i = 0; i < n >> 1; i++) {
            intFile.putInt(i, i);
        }
        intFile.close();

        intFile = IntWriteFile.ofSync(key, newConnector);
        for (int i = n >> 1; i < n; i++) {
            intFile.putInt(i, i);
        }
        intFile.close();

        IntReadFile intReadFile = new IntReadFile(key, connector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, intReadFile.getInt(i));
        }
    }

    @Test
    public void testLong() {
        LongWriteFile longWriteFile = LongWriteFile.ofSync(key, connector);
        for (int i = 0; i < n >> 1; i++) {
            longWriteFile.putLong(i, i);
        }
        longWriteFile.close();

        longWriteFile = LongWriteFile.ofSync(key, newConnector);
        for (int i = n >> 1; i < n; i++) {
            longWriteFile.putLong(i, i);
        }
        longWriteFile.close();

        LongReadFile longReadFile = new LongReadFile(key, connector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, longReadFile.getLong(i));
        }
    }

    @Test
    public void testDouble() {
        DoubleWriteFile doubleWriteFile = DoubleWriteFile.ofSync(key, connector);
        for (int i = 0; i < n >> 1; i++) {
            doubleWriteFile.putDouble(i, i);
        }
        doubleWriteFile.close();

        doubleWriteFile = DoubleWriteFile.ofSync(key, newConnector);
        for (int i = n >> 1; i < n; i++) {
            doubleWriteFile.putDouble(i, i);
        }
        doubleWriteFile.close();

        DoubleReadFile doubleReadFile = new DoubleReadFile(key, connector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, doubleReadFile.getDouble(i), 0);
        }
    }
}