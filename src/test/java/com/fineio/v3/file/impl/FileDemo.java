package com.fineio.v3.file.impl;

import com.fineio.v3.connector.FileConnector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.file.impl.read.ByteReadFile;
import com.fineio.v3.file.impl.read.DoubleReadFile;
import com.fineio.v3.file.impl.read.IntReadFile;
import com.fineio.v3.file.impl.read.LongReadFile;
import com.fineio.v3.file.impl.write.ByteWriteFile;
import com.fineio.v3.file.impl.write.DoubleWriteFile;
import com.fineio.v3.file.impl.write.IntWriteFile;
import com.fineio.v3.file.impl.write.LongWriteFile;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2019/4/16
 */
public class FileDemo {
    private FileKey key = new FileKey(System.getProperty("user.dir"), "1");
    private FileConnector connector = new FileConnector(10);
    private int n = 1 << 10;

    @Test
    public void testByte() throws Exception {
        ByteWriteFile byteWriteFile = new ByteWriteFile(key, connector);
        IntStream.range(-128, 128).forEachOrdered(i -> byteWriteFile.putByte(i + 128, (byte) i));
        byteWriteFile.close();

        TimeUnit.SECONDS.sleep(1);

        ByteReadFile byteReadFile = new ByteReadFile(key, connector);
        for (int i = 0; i < 256; i++) {
            assertEquals(i - 128, byteReadFile.getByte(i));
        }
    }

    @Test
    public void testInt() throws Exception {
        IntWriteFile intWriteFile = new IntWriteFile(key, connector);
        IntStream.range(0, n).forEachOrdered(i -> intWriteFile.putInt(i, i));
        intWriteFile.close();

        TimeUnit.SECONDS.sleep(1);

        IntReadFile intReadFile = new IntReadFile(key, connector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, intReadFile.getInt(i));
        }
    }

    @Test
    public void testLong() throws Exception {
        LongWriteFile longWriteFile = new LongWriteFile(key, connector);
        LongStream.range(0, n).forEachOrdered(i -> longWriteFile.putLong(i, i));
        longWriteFile.close();

        TimeUnit.SECONDS.sleep(1);

        LongReadFile longReadFile = new LongReadFile(key, connector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, longReadFile.getLong(i));
        }
    }

    @Test
    public void testDouble() throws Exception {
        DoubleWriteFile doubleWriteFile = new DoubleWriteFile(key, connector);
        LongStream.range(0, n).forEachOrdered(i -> doubleWriteFile.putDouble(i, i));
        doubleWriteFile.close();

        TimeUnit.SECONDS.sleep(1);

        DoubleReadFile doubleReadFile = new DoubleReadFile(key, connector);
        for (int i = 0; i < n; i++) {
            assertEquals(i, doubleReadFile.getDouble(i), 0);
        }
    }
}