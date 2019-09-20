package com.fineio.v2.io.file;


import com.fineio.storage.Connector;
import com.fineio.v2.io.BaseBuffer;
import com.fineio.v2.io.Buffer;
import com.fineio.v2.io.ByteBuffer;
import com.fineio.v2.io.CharBuffer;
import com.fineio.v2.io.DoubleBuffer;
import com.fineio.v2.io.FloatBuffer;
import com.fineio.v2.io.IntBuffer;
import com.fineio.v2.io.LongBuffer;
import com.fineio.v2.io.ShortBuffer;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class WriteIOFileV2<B extends Buffer> extends IOFileV2<B> {
    private final boolean sync;

    WriteIOFileV2(Connector connector, URI uri, FileModel model, boolean sync) {
        super(connector, uri, model);
        this.sync = sync;
        this.block_size_offset = (byte) (connector.getBlockOffset() - model.offset());
        single_block_len = (1L << block_size_offset) - 1;
    }

    public static final <E extends BaseBuffer> WriteIOFileV2<E> createFineIO(Connector connector, URI uri, FileModel model, boolean sync) {
        return new WriteIOFileV2<E>(connector, uri, model, sync);
    }

    @Override
    protected FileLevel getFileLevel() {
        return FileLevel.WRITE;
    }

    @Override
    protected Buffer initBuffer(int index) {
        synchronized (this) {
            if (null == buffers[index]) {
                BaseBuffer buffer = model.createBuffer(connector, createIndexBlock(index), block_size_offset, sync);
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
