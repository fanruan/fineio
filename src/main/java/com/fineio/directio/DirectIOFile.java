package com.fineio.directio;

import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.FileModel;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/10/2
 */
public abstract class DirectIOFile<B extends Buffer> {
    protected Connector connector;
    protected URI uri;
    protected volatile Buffer buffer;

    protected FileModel model;
    protected volatile boolean released = false;

    protected DirectIOFile(Connector connector, URI uri, FileModel model) {
        this.connector = connector;
        this.uri = uri;
        this.model = model;
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<DoubleBuffer> file, double d) {
        ((DoubleBuffer.DoubleWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<ByteBuffer> file, byte d) {
        ((ByteBuffer.ByteWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<CharBuffer> file, char d) {
        ((CharBuffer.CharWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<FloatBuffer> file, float d) {
        ((FloatBuffer.FloatWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<LongBuffer> file, long d) {
        ((LongBuffer.LongWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<IntBuffer> file, int d) {
        ((IntBuffer.IntWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<ShortBuffer> file, short d) {
        ((ShortBuffer.ShortWriteBuffer) file.getBuffer()).put(d);
    }


    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<DoubleBuffer> file, int p, double d) {
        ((DoubleBuffer.DoubleWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<ByteBuffer> file, int p, byte d) {
        ((ByteBuffer.ByteWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<CharBuffer> file, int p, char d) {
        ((CharBuffer.CharWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<FloatBuffer> file, int p, float d) {
        ((FloatBuffer.FloatWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<LongBuffer> file, int p, long d) {
        ((LongBuffer.LongWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<IntBuffer> file, int p, int d) {
        ((IntBuffer.IntWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<ShortBuffer> file, int p, short d) {
        ((ShortBuffer.ShortWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static long getLong(DirectIOFile<LongBuffer> file, int p) {
        return ((LongBuffer.LongReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static int getInt(DirectIOFile<IntBuffer> file, int p) {
        return ((IntBuffer.IntReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static char getChar(DirectIOFile<CharBuffer> file, int p) {
        return ((CharBuffer.CharReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static double getDouble(DirectIOFile<DoubleBuffer> file, int p) {
        return ((DoubleBuffer.DoubleReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static float getFloat(DirectIOFile<FloatBuffer> file, int p) {
        return ((FloatBuffer.FloatReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static byte getByte(DirectIOFile<ByteBuffer> file, int p) {
        return ((ByteBuffer.ByteReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static short getShort(DirectIOFile<ShortBuffer> file, int p) {
        return ((ShortBuffer.ShortReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 获取设置的path
     */
    public String getPath() {
        return uri.getPath();
    }

    private Buffer getBuffer() {
        return buffer != null ? buffer : initBuffer();
    }

    protected abstract Buffer initBuffer();

    @Override
    protected void finalize() {
        //防止没有执行close导致内存泄露
        close();
    }

    public void close() {
        synchronized (this) {
            if (released) {
                return;
            }
            closeChild();
            released = true;
        }
    }

    protected abstract void closeChild();

    public final int length() {
        if (null != buffer) {
            return buffer.getLength();
        }
        return 0;
    }
}
