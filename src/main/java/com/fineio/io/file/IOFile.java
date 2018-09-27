package com.fineio.io.file;

import com.fineio.base.Bits;
import com.fineio.io.Buffer;
import com.fineio.io.BufferW;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public abstract class IOFile<B extends Buffer> {
    protected final static int STEP_LEN = MemoryConstants.STEP_LONG;
    private final static int HEAD_LEN = STEP_LEN + 1;
    /**
     * 内部路径 key
     */
    protected URI uri;
    /**
     * 连接器
     */
    protected Connector connector;
    protected FileModel model;
    /**
     * 分多少块
     */
    protected int blocks;
    /**
     * 每块尺寸的大小的偏移量 2的N次方
     */
    protected byte block_size_offset;
    /**
     * 单个block的大小
     */
    protected long single_block_len;
    protected volatile boolean released = false;
    protected volatile Buffer[] buffers;
    private volatile int bufferWriteIndex = -1;

    IOFile(Connector connector, URI uri, FileModel model) {
        if (uri == null || connector == null || model == null) {
            throw new RuntimeException("uri  or connector or model can't be null");
        }
        this.connector = connector;
        this.uri = URI.create(uri.getPath() + "/");
        this.model = model;
    }

    public final static int getInt(IOFile<IntBuffer> file, long p) {
        return ((IntBuffer.IntReadBuffer) file.getBuffer((int) (p >> file.block_size_offset))).get((int) (p & file.single_block_len));
    }

    public final static long getLong(IOFile<LongBuffer> file, long p) {
        return ((LongBuffer.LongReadBuffer) file.getBuffer((int) (p >> file.block_size_offset))).get((int) (p & file.single_block_len));
    }

    public final static double getDouble(IOFile<DoubleBuffer> file, long p) {
        return ((DoubleBuffer.DoubleReadBuffer) file.getBuffer((int) (p >> file.block_size_offset))).get((int) (p & file.single_block_len));
    }

    public final static byte getByte(IOFile<ByteBuffer> file, long p) {
        return ((ByteBuffer.ByteReadBuffer) file.getBuffer((int) (p >> file.block_size_offset))).get((int) (p & file.single_block_len));
    }

    public final static void put(IOFile<IntBuffer> file, int d) {
        int result = 0;
        if (file.buffers == null || file.buffers.length == 0) {
            result = 0;
        } else {
            result = file.calBufferIndex();
        }
        ((IntBuffer.IntWriteBuffer) file.getBuffer(file.checkBuffer(result))).put(d);
    }

    public final static void put(IOFile<ByteBuffer> file, byte d) {
        int result = 0;
        if (file.buffers == null || file.buffers.length == 0) {
            result = 0;
        } else {
            result = file.calBufferIndex();
        }
        ((ByteBuffer.ByteWriteBuffer) file.getBuffer(file.checkBuffer(result))).put(d);
    }

    public final static void put(IOFile<LongBuffer> file, long d) {
        int result = 0;
        if (file.buffers == null || file.buffers.length == 0) {
            result = 0;
        } else {
            result = file.calBufferIndex();
        }
        ((LongBuffer.LongWriteBuffer) file.getBuffer(file.checkBuffer(result))).put(d);
    }

    public final static void put(IOFile<DoubleBuffer> file, double d) {
        int result = 0;
        if (file.buffers == null || file.buffers.length == 0) {
            result = 0;
        } else {
            result = file.calBufferIndex();
        }
        ((DoubleBuffer.DoubleWriteBuffer) file.getBuffer(file.checkBuffer(result))).put(d);
    }

    public final static void put(IOFile<ShortBuffer> file, short d) {
        int result = 0;
        if (file.buffers == null || file.buffers.length == 0) {
            result = 0;
        } else {
            result = file.calBufferIndex();
        }
        ((ShortBuffer.ShortWriteBuffer) file.getBuffer(file.checkBuffer(result))).put(d);
    }

    public final static void put(IOFile<FloatBuffer> file, float d) {
        int result = 0;
        if (file.buffers == null || file.buffers.length == 0) {
            result = 0;
        } else {
            result = file.calBufferIndex();
        }
        ((FloatBuffer.FloatWriteBuffer) file.getBuffer(file.checkBuffer(result))).put(d);
    }

    public final static void put(IOFile<CharBuffer> file, char d) {
        int result = 0;
        if (file.buffers == null || file.buffers.length == 0) {
            result = 0;
        } else {
            result = file.calBufferIndex();
        }
        ((CharBuffer.CharWriteBuffer) file.getBuffer(file.checkBuffer(result))).put(d);
    }

    public final static char getChar(IOFile<CharBuffer> file, long p) {
        return ((CharBuffer.CharReadBuffer) file.getBuffer((int) (p >> file.block_size_offset))).get((int) (p & file.single_block_len));
    }

    public final static float getFloat(IOFile<FloatBuffer> file, long p) {
        return ((FloatBuffer.FloatReadBuffer) file.getBuffer((int) (p >> file.block_size_offset))).get((int) (p & file.single_block_len));
    }

    public final static short getShort(IOFile<ShortBuffer> file, long p) {
        return ((ShortBuffer.ShortReadBuffer) file.getBuffer((int) (p >> file.block_size_offset))).get((int) (p & file.single_block_len));
    }

    protected final int calBufferIndex() {
        int len = buffers.length;
        if (((BufferW) buffers[len - 1]).full()) {
            return triggerWrite(len);
        } else {
            return len - 1;
        }
    }

    private final int checkBuffer(int index) {
        return inRange(index) ? index : createBufferArrayInRange(index);
    }

    private final int createBufferArrayInRange(int index) {
        Buffer[] buffers = this.buffers;
        createBufferArray(index + 1);
        if (buffers != null) {
            System.arraycopy(buffers, 0, this.buffers, 0, buffers.length);
        }
        return index;
    }

    private final boolean inRange(int index) {
        return buffers != null && buffers.length > index;
    }

    private final int triggerWrite(int len) {
        checkWrite(len);
        return len;
    }

    private final Buffer getBuffer(int i) {
        if (i < buffers.length && i > -1) {
            if (buffers[i] != null) {
                return buffers[i];
            }
            return initBuffer(i);
        } else {
            throw new IndexOutOfBoundsException("" + i);
        }
    }

    protected abstract Buffer initBuffer(int index);

    private final int checkIndex(int index) {
        if (index > -1 && index < blocks) {
            return index;
        }
        throw new IndexOutOfBoundsException("" + index);
    }

    protected FileBlock createIndexBlock(int index) {
        return new FileBlock(uri, String.valueOf(index));
    }

    protected final FileBlock createHeadBlock() {
        return new FileBlock(uri, "head");
    }

    /**
     * 注意所有写方法并不支持多线程操作，仅读的方法支持
     *
     * @param size
     */
    protected final void createBufferArray(int size) {
        this.blocks = size;
        this.buffers = (B[]) new Buffer[size];
    }

    final void checkWrite(int len) {
        if (bufferWriteIndex != len) {
            if (bufferWriteIndex != -1) {
                ((BufferW) buffers[bufferWriteIndex]).write();
            }
            bufferWriteIndex = len;
        }
    }

    protected final void writeHeader() {
        FileBlock block = createHeadBlock();
        byte[] bytes = new byte[HEAD_LEN];
        Bits.putInt(bytes, 0, buffers == null ? 0 : buffers.length);
        bytes[STEP_LEN] = (byte) (block_size_offset + model.offset());
        try {
            connector.write(block, bytes);
        } catch (Throwable e) {
        }
    }

    public void close() {
        if (null != buffers) {
            for (int i = 0; i < buffers.length; i++) {
                if (null != buffers[i]) {
                    buffers[i].close();
                    buffers[i] = null;
                }
            }
        }
    }

    public final boolean exists() {
        synchronized (this) {
            boolean exists = connector.exists(createHeadBlock());
            boolean v = connector.exists(createIndexBlock(0));
            if (exists) {
                exists = v;
            }
            released = true;
            return exists;
        }
    }
}
