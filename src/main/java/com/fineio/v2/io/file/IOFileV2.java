package com.fineio.v2.io.file;

import com.fineio.base.Bits;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileConstants;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;
import com.fineio.v2.io.Buffer;
import com.fineio.v2.io.BufferW;
import com.fineio.v2.io.ByteBuffer;
import com.fineio.v2.io.CharBuffer;
import com.fineio.v2.io.DoubleBuffer;
import com.fineio.v2.io.FloatBuffer;
import com.fineio.v2.io.IntBuffer;
import com.fineio.v2.io.LongBuffer;
import com.fineio.v2.io.ShortBuffer;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/9/20
 */
public abstract class IOFileV2<B extends Buffer> {
    protected final static int STEP_LEN = MemoryConstants.STEP_LONG;
    private final static int HEAD_LEN = STEP_LEN + 1;
    public static Map<FileBlock, byte[]> HEAD_MAP = new ConcurrentHashMap<FileBlock, byte[]>();
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
    private volatile boolean close = false;
    private volatile int bufferWriteIndex = -1;

    IOFileV2(Connector connector, URI uri, FileModel model) {
        if (uri == null || connector == null || model == null) {
            throw new RuntimeException("uri  or connector or model can't be null");
        }
        this.connector = connector;
        this.uri = URI.create(uri.getPath() + "/");
        this.model = model;
    }

//    static void checkAppendIOFile(IOFile file) {
//        if (file instanceof AppendIOFile) {
//            throw new IllegalArgumentException(String.format("File %s is an append file", file.uri));
//        }
//    }

    public final static int getInt(IOFileV2<IntBuffer> file, long p) {

        return ((IntBuffer.IntReadBuffer) file.getBuffer(file.checkBufferForRead((int) (p >> file.block_size_offset)))).get((int) (p & file.single_block_len));
    }

    public final static long getLong(IOFileV2<LongBuffer> file, long p) {

        return ((LongBuffer.LongReadBuffer) file.getBuffer(file.checkBufferForRead((int) (p >> file.block_size_offset)))).get((int) (p & file.single_block_len));
    }

    public final static double getDouble(IOFileV2<DoubleBuffer> file, long p) {

        return ((DoubleBuffer.DoubleReadBuffer) file.getBuffer(file.checkBufferForRead((int) (p >> file.block_size_offset)))).get((int) (p & file.single_block_len));
    }

    public final static byte getByte(IOFileV2<ByteBuffer> file, long p) {

        return ((ByteBuffer.ByteReadBuffer) file.getBuffer(file.checkBufferForRead((int) (p >> file.block_size_offset)))).get((int) (p & file.single_block_len));
    }

    public final static void put(IOFileV2<IntBuffer> file, int d) {
        ((IntBuffer.IntWriteBuffer) file.getBuffer(file.checkBuffer(file.cw()))).put(d);
    }

    public final static void put(IOFileV2<ByteBuffer> file, byte d) {
        ((ByteBuffer.ByteWriteBuffer) file.getBuffer(file.checkBuffer(file.cw()))).put(d);
    }

    public final static void put(IOFileV2<LongBuffer> file, long d) {
        ((LongBuffer.LongWriteBuffer) file.getBuffer(file.checkBuffer(file.cw()))).put(d);
    }

    public final static void put(IOFileV2<DoubleBuffer> file, double d) {
        ((DoubleBuffer.DoubleWriteBuffer) file.getBuffer(file.checkBuffer(file.cw()))).put(d);
    }

    public final static void put(IOFileV2<ShortBuffer> file, short d) {
        ((ShortBuffer.ShortWriteBuffer) file.getBuffer(file.checkBuffer(file.cw()))).put(d);
    }

    public final static void put(IOFileV2<FloatBuffer> file, float d) {
        ((FloatBuffer.FloatWriteBuffer) file.getBuffer(file.checkBuffer(file.cw()))).put(d);
    }

    public final static void put(IOFileV2<CharBuffer> file, char d) {
        ((CharBuffer.CharWriteBuffer) file.getBuffer(file.checkBuffer(file.cw()))).put(d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public final static void put(IOFileV2<DoubleBuffer> file, long p, double d) {
        switch (file.getFileLevel()) {
            case READ:
                throw new UnsupportedOperationException();
            case WRITE:
                ((WriteIOFileV2<DoubleBuffer>) file).put(p, d);
                break;
            case APPEND:
                put(file, d);
            default:
        }
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public final static void put(IOFileV2<CharBuffer> file, long p, char d) {
        switch (file.getFileLevel()) {
            case READ:
                throw new UnsupportedOperationException();
            case WRITE:
                ((WriteIOFileV2<CharBuffer>) file).put(p, d);
                break;
            case APPEND:
                put(file, d);
            default:
        }

    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public final static void put(IOFileV2<LongBuffer> file, long p, long d) {
        switch (file.getFileLevel()) {
            case READ:
                throw new UnsupportedOperationException();
            case WRITE:
                ((WriteIOFileV2<LongBuffer>) file).put(p, d);
                break;
            case APPEND:
                put(file, d);
            default:
        }

    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public final static void put(IOFileV2<IntBuffer> file, long p, int d) {
        switch (file.getFileLevel()) {
            case READ:
                throw new UnsupportedOperationException();
            case WRITE:
                ((WriteIOFileV2<IntBuffer>) file).put(p, d);
                break;
            case APPEND:
                put(file, d);
            default:
        }

    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public final static void put(IOFileV2<ByteBuffer> file, long p, byte d) {
        switch (file.getFileLevel()) {
            case READ:
                throw new UnsupportedOperationException();
            case WRITE:
                ((WriteIOFileV2<ByteBuffer>) file).put(p, d);
                break;
            case APPEND:
                put(file, d);
            default:
        }

    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public final static void put(IOFileV2<ShortBuffer> file, long p, short d) {
        switch (file.getFileLevel()) {
            case READ:
                throw new UnsupportedOperationException();
            case WRITE:
                ((WriteIOFileV2<ShortBuffer>) file).put(p, d);
                break;
            case APPEND:
                put(file, d);
            default:
        }

    }

    public final static char getChar(IOFileV2<CharBuffer> file, long p) {

        return ((CharBuffer.CharReadBuffer) file.getBuffer(file.checkBufferForRead((int) (p >> file.block_size_offset)))).get((int) (p & file.single_block_len));
    }

    public final static float getFloat(IOFileV2<FloatBuffer> file, long p) {

        return ((FloatBuffer.FloatReadBuffer) file.getBuffer(file.checkBufferForRead((int) (p >> file.block_size_offset)))).get((int) (p & file.single_block_len));
    }

    public final static short getShort(IOFileV2<ShortBuffer> file, long p) {

        return ((ShortBuffer.ShortReadBuffer) file.getBuffer(file.checkBufferForRead((int) (p >> file.block_size_offset)))).get((int) (p & file.single_block_len));
    }

    public final static void put(IOFileV2<FloatBuffer> file, long p, float d) {
        switch (file.getFileLevel()) {
            case READ:
                throw new UnsupportedOperationException();
            case WRITE:
                ((WriteIOFileV2<FloatBuffer>) file).put(p, d);
                break;
            case APPEND:
                put(file, d);
            default:
        }
    }

    private int cw() {
        int result = 0;
        if (buffers == null || buffers.length == 0) {
            result = 0;
        } else {
            int len = buffers.length;
            if (null != buffers[len - 1] && ((BufferW) buffers[len - 1]).full()) {
                result = triggerWrite(len);
            } else {
                result = len - 1;
            }
        }
        return result;
    }

    protected abstract FileLevel getFileLevel();

    protected final int checkBuffer(int index) {
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

    protected final Buffer getBuffer(int i) {
        if (i < buffers.length && i > -1) {
            if (buffers[i] != null) {
                return buffers[i];
            }
            return initBuffer(i);
        } else {
            throw new IndexOutOfBoundsException(uri.getPath() + i);
        }
    }

    private int checkBufferForRead(int index) {
        if (null == buffers) {
            ((ReadIOFileV2) this).readHeader(model.offset());
        }
        if (buffers.length > index) {
            return index;
        }
        throw new IndexOutOfBoundsException(uri.getPath() + index);
    }

    protected abstract Buffer initBuffer(int index);

    protected FileBlock createIndexBlock(int index) {
        return new FileBlock(uri, String.valueOf(index));
    }

    protected final FileBlock createHeadBlock() {
        return new FileBlock(uri, FileConstants.HEAD);
    }

    /**
     * 注意所有写方法并不支持多线程操作，仅读的方法支持
     *
     * @param size
     */
    protected final void createBufferArray(int size) {
        this.blocks = size;
        this.buffers = new Buffer[size];
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
        if (!close) {
            FileBlock block = createHeadBlock();
            byte[] bytes = new byte[HEAD_LEN];
            Bits.putInt(bytes, 0, buffers == null ? 0 : buffers.length);
            bytes[STEP_LEN] = (byte) (block_size_offset + model.offset());
            try {
                HEAD_MAP.put(block, bytes);
                connector.write(block, bytes);
//                FineIOLoggers.getLogger().error("FineIO write " + uri + " block size: " + buffers.length);
            } catch (Throwable e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
    }

    public void close() {
        if (!close) {
            synchronized (this) {
                if (!close && null != buffers) {
                    for (int i = 0; i < buffers.length; i++) {
                        if (null != buffers[i]) {
                            buffers[i].close();
                            buffers[i] = null;
                        }
                    }
                    close = true;
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

    @Override
    protected void finalize() {
        //防止没有执行close导致内存泄露
        close();
    }

    public final String getPath() {
        return uri.getPath();
    }
}
