package com.fineio.io.file;


import com.fineio.io.BaseBuffer;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class WriteIOFile<B extends Buffer> extends IOFile<B> {
    WriteIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
        this.block_size_offset = (byte) (connector.getBlockOffset() - model.offset());
        single_block_len = (1L << block_size_offset) - 1;
    }

    @Override
    protected FileLevel getFileLevel() {
        return FileLevel.WRITE;
    }

    public static final <E extends BaseBuffer> WriteIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new WriteIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer initBuffer(int index) {
        synchronized (this) {
            if (null == buffers[index]) {
                BaseBuffer buffer = model.createBuffer(connector, createIndexBlock(index), block_size_offset);
                buffers[index] = buffer.asWrite();
            }
            return buffers[index];
        }
    }

    @Override
    public final void close() {
        writeHeader();
        super.close();
    }

    protected void put(long p, double d) {
        int len = (int) (p >> this.block_size_offset);
        if (len > 0) {
            this.checkWrite(len);
        }
        ((DoubleBuffer.DoubleWriteBuffer) this.getBuffer(this.checkBuffer(len))).put((int) (p & this.single_block_len), d);
    }

    protected void put(long p, byte d) {
        int len = (int) (p >> this.block_size_offset);
        if (len > 0) {
            this.checkWrite(len);
        }
        ((ByteBuffer.ByteWriteBuffer) this.getBuffer(this.checkBuffer(len))).put((int) (p & this.single_block_len), d);
    }

    protected void put(long p, int d) {
        int len = (int) (p >> this.block_size_offset);
        if (len > 0) {
            this.checkWrite(len);
        }
        ((IntBuffer.IntWriteBuffer) this.getBuffer(this.checkBuffer(len))).put((int) (p & this.single_block_len), d);
    }

    protected void put(long p, long d) {
        int len = (int) (p >> this.block_size_offset);
        if (len > 0) {
            this.checkWrite(len);
        }
        ((LongBuffer.LongWriteBuffer) this.getBuffer(this.checkBuffer(len))).put((int) (p & this.single_block_len), d);
    }

    protected void put(long p, short d) {
        int len = (int) (p >> this.block_size_offset);
        if (len > 0) {
            this.checkWrite(len);
        }
        ((ShortBuffer.ShortWriteBuffer) this.getBuffer(this.checkBuffer(len))).put((int) (p & this.single_block_len), d);
    }

    protected void put(long p, char d) {
        int len = (int) (p >> this.block_size_offset);
        if (len > 0) {
            this.checkWrite(len);
        }
        ((CharBuffer.CharWriteBuffer) this.getBuffer(this.checkBuffer(len))).put((int) (p & this.single_block_len), d);
    }

    protected void put(long p, float d) {
        int len = (int) (p >> this.block_size_offset);
        if (len > 0) {
            this.checkWrite(len);
        }
        ((FloatBuffer.FloatWriteBuffer) this.getBuffer(this.checkBuffer(len))).put((int) (p & this.single_block_len), d);
    }
}
