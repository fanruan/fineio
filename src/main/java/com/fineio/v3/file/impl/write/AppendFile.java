package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.DirectBuffer;

import java.io.Closeable;

/**
 * @author yee
 */
class AppendFile<B extends DirectBuffer> implements Closeable {
    private int curPos;

    private final WriteFile<B> writeFile;

    public AppendFile(WriteFile<B> writeFile) {
        this.writeFile = writeFile;
    }

    public void putByte(byte value) {
        ((ByteWriteFile) writeFile).putByte(curPos++, value);
    }

    public void putInt(int value) {
        ((IntWriteFile) writeFile).putInt(curPos++, value);
    }

    public void putLong(long value) {
        ((LongWriteFile) writeFile).putLong(curPos++, value);
    }

    public void putDouble(double value) {
        ((DoubleWriteFile) writeFile).putDouble(curPos++, value);
    }

    @Override
    public void close() {
        writeFile.close();
    }

    public void delete() {
        writeFile.delete();
    }
}