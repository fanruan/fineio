package com.fineio.io.file;

import com.fineio.base.Bits;
import com.fineio.cache.BufferPrivilege;
import com.fineio.cache.CacheManager;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.IOSetException;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FileModel;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.read.ByteReadBuffer;
import com.fineio.io.read.CharReadBuffer;
import com.fineio.io.read.DoubleReadBuffer;
import com.fineio.io.read.FloatReadBuffer;
import com.fineio.io.read.IntReadBuffer;
import com.fineio.io.read.LongReadBuffer;
import com.fineio.io.read.ShortReadBuffer;
import com.fineio.io.write.ByteWriteBuffer;
import com.fineio.io.write.CharWriteBuffer;
import com.fineio.io.write.DoubleWriteBuffer;
import com.fineio.io.write.FloatWriteBuffer;
import com.fineio.io.write.IntWriteBuffer;
import com.fineio.io.write.LongWriteBuffer;
import com.fineio.io.write.ShortWriteBuffer;
import com.fineio.io.write.WriteOnlyBuffer;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.io.Closeable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.URI;

/**
 * @author yee
 * @date 2018/6/1
 */
public abstract class IOFile<E extends Buffer> implements Closeable {
    private final static int HEAD_LEN = MemoryConstants.STEP_LONG + 1;
    /**
     * 内部路径 key
     */
    protected URI uri;
    /**
     * 连接器
     */
    protected Connector connector;
    /**
     * 分多少块
     */
    protected int blocks;
    /**
     * 每块尺寸的大小的偏移量 2的N次方
     */
    protected byte block_size_offset;
    /**
     * 读的类型
     */
    protected FileModel model;
    /**
     * 单个block的大小
     */
    protected long single_block_len;
    protected volatile WeakReference<Buffer>[] buffers;
    protected volatile boolean released = false;
    private volatile int bufferWriteIndex = -1;

    IOFile(Connector connector, URI uri, FileModel model) {
        if (uri == null || connector == null || model == null) {
            throw new IOSetException("uri  or connector or model can't be null");
        }
        this.connector = connector;
        this.uri = URI.create(uri.getPath() + "/");
        this.model = model;
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(IOFile<DoubleBuffer> file, double d) {
        ((DoubleWriteBuffer) file.getBuffer(file.checkBuffer(file.gi()))).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(IOFile<ByteBuffer> file, byte d) {
        ((ByteWriteBuffer) file.getBuffer(file.checkBuffer(file.gi()))).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(IOFile<CharBuffer> file, char d) {
        ((CharWriteBuffer) file.getBuffer(file.checkBuffer(file.gi()))).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(IOFile<FloatBuffer> file, float d) {
        ((FloatWriteBuffer) file.getBuffer(file.checkBuffer(file.gi()))).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(IOFile<LongBuffer> file, long d) {
        ((LongWriteBuffer) file.getBuffer(file.checkBuffer(file.gi()))).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(IOFile<IntBuffer> file, int d) {
        ((IntWriteBuffer) file.getBuffer(file.checkBuffer(file.gi()))).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(IOFile<ShortBuffer> file, short d) {
        ((ShortWriteBuffer) file.getBuffer(file.checkBuffer(file.gi()))).put(d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public void put(long p, double d) {
        ((DoubleWriteBuffer) this.getBuffer(this.checkBuffer(this.giw(p)))).put(this.gp(p), d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public void put(long p, byte d) {
        ((ByteWriteBuffer) this.getBuffer(this.checkBuffer(this.giw(p)))).put(this.gp(p), d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public void put(long p, char d) {
        ((CharWriteBuffer) this.getBuffer(this.checkBuffer(this.giw(p)))).put(this.gp(p), d);
    }

    /**
     * 随机写
     *

     * @param p
     * @param d
     */
    public void put(long p, float d) {
        ((FloatWriteBuffer) this.getBuffer(this.checkBuffer(this.giw(p)))).put(this.gp(p), d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public void put(long p, long d) {
        ((LongWriteBuffer) this.getBuffer(this.checkBuffer(this.giw(p)))).put(this.gp(p), d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public void put(long p, int d) {
        ((IntWriteBuffer) this.getBuffer(this.checkBuffer(this.giw(p)))).put(this.gp(p), d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    public void put(long p, short d) {
        ((ShortWriteBuffer) this.getBuffer(this.checkBuffer(this.giw(p)))).put(this.gp(p), d);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static long getLong(IOFile<LongBuffer> file, long p) {
        return ((LongReadBuffer) file.getBuffer(file.gi(p))).get(file.gp(p));
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static int getInt(IOFile<IntBuffer> file, long p) {
        return ((IntReadBuffer) file.getBuffer(file.gi(p))).get(file.gp(p));
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static char getChar(IOFile<CharBuffer> file, long p) {
        return ((CharReadBuffer) file.getBuffer(file.gi(p))).get(file.gp(p));
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static double getDouble(IOFile<DoubleBuffer> file, long p) {
        return ((DoubleReadBuffer) file.getBuffer(file.gi(p))).get(file.gp(p));
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static float getFloat(IOFile<FloatBuffer> file, long p) {
        return ((FloatReadBuffer) file.getBuffer(file.gi(p))).get(file.gp(p));
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static byte getByte(IOFile<ByteBuffer> file, long p) {
        return ((ByteReadBuffer) file.getBuffer(file.gi(p))).get(file.gp(p));
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static short getShort(IOFile<ShortBuffer> file, long p) {
        return ((ShortReadBuffer) file.getBuffer(file.gi(p))).get(file.gp(p));
    }

    /**
     * 获取设置的path
     */
    public String getPath() {
        return uri.getPath();
    }

    protected final FileBlock createHeadBlock() {
        return new FileBlock(uri, FileConstants.HEAD);
    }

    //触发写
    final int giw(long p) {
        int len = (int) (p >> block_size_offset);
        if (len > 0) {
            checkWrite(len);
        }
        return len;
    }

    final void checkWrite(int len) {
        if (bufferWriteIndex != len) {
            if (bufferWriteIndex != -1) {
                ((WriteOnlyBuffer) buffers[bufferWriteIndex].get()).write();
            }
            bufferWriteIndex = len;
        }
    }

    final int gi() {
        if (buffers == null || buffers.length == 0) {
            return 0;
        }
        int len = buffers.length - 1;
        WriteOnlyBuffer buffer = null;
        if (null == buffers[len] || null == buffers[len].get()) {
            buffer = (WriteOnlyBuffer) initBuffer(len);
        } else {
            buffer = (WriteOnlyBuffer) buffers[len].get();
        }
        return buffer.full() ? triggerWrite(len + 1) : len;
    }

    final int triggerWrite(int len) {
        checkWrite(len);
        return len;
    }

    /**
     * 注意所有写方法并不支持多线程操作，仅读的方法支持
     *
     * @param size
     */
    protected final void createBufferArray(int size) {
        this.blocks = size;
        this.buffers = new WeakReference[size];
    }

    private boolean inRange(int index) {
        return buffers != null && buffers.length > index;
    }

    protected final int checkBuffer(int index) {
        if (index < 0) {
            throw new BufferIndexOutOfBoundsException(index);
        }
        return inRange(index) ? index : createBufferArrayInRange(index);
    }

    private int createBufferArrayInRange(int index) {
        WeakReference<Buffer>[] buffers = this.buffers;
        createBufferArray(index + 1);
        if (buffers != null) {
            System.arraycopy(buffers, 0, this.buffers, 0, buffers.length);
        }
        return index;
    }

    //读
    protected int gi(long p) {
        return (int) (p >> block_size_offset);
    }

    protected final int gp(long p) {
        return (int) (p & single_block_len);
    }

    protected final Buffer getBuffer(int index) {
        if (buffers[checkIndex(index)] != null) {
            synchronized (buffers[index]) {
                if (null != buffers[index].get()) {
                    return buffers[index].get();
                }
            }
        }
        return initBuffer(index);
    }

    private int checkIndex(int index) {
        if (index > -1 && index < blocks) {
            return index;
        }
        throw new BufferIndexOutOfBoundsException(index);
    }

    private Buffer initBuffer(int index) {
        synchronized (this) {
        if (buffers[index] == null || buffers[index].get() == null) {
            buffers[index] = new WeakReference<Buffer>(createBuffer(index), (ReferenceQueue<? super Buffer>) CacheManager.getInstance().referenceQueue);
            }
        return buffers[index].get();
        }
    }

    protected abstract Buffer createBuffer(int index);

    /**
     * 判断是否存在
     *
     * @return
     */
    public boolean exists() {
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

    protected abstract BufferPrivilege getLevel();

    protected FileBlock createIndexBlock(int index) {
        return new FileBlock(uri, String.valueOf(index));
    }

    final FileModel getModel() {
        return model;
    }

    protected void writeHeader() {
        FileBlock block = createHeadBlock();
        byte[] bytes = new byte[HEAD_LEN];
        Bits.putInt(bytes, 0, buffers == null ? 0 : buffers.length);
        bytes[MemoryConstants.STEP_LONG] = (byte) (block_size_offset + model.offset());
        try {
            connector.write(block, bytes);
        } catch (Throwable e) {
            FineIOLoggers.getLogger().error(e);
        }
    }

    protected void finalize() {
        //防止没有执行close导致内存泄露
        close();
    }

    @Override
    public void close() {
        synchronized (this) {
            if (released) {
                return;
            }
            writeHeader();
            closeChild(false);
            released = true;
        }
    }

    public void closeAndClear() {
        synchronized (this) {
            if (released) {
                return;
            }
            writeHeader();
            closeChild(true);
            released = true;
        }
    }

    public long fileSize() {
        long size = 0;
        if (buffers != null) {
            for (WeakReference<Buffer> buffer : buffers) {
                if (null != buffer && null != buffer.get()) {
                    size += buffer.get().getAllocateSize();
                }
            }
        }
        return size;
    }

    protected abstract void closeChild(boolean clear);
}
