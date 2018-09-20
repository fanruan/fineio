package com.fineio.directio;

import com.fineio.storage.Connector;
import com.fineio.v2.io.Buffer;
import com.fineio.v2.io.ByteBuffer;
import com.fineio.v2.io.CharBuffer;
import com.fineio.v2.io.DoubleBuffer;
import com.fineio.v2.io.FileModel;
import com.fineio.v2.io.FloatBuffer;
import com.fineio.v2.io.IntBuffer;
import com.fineio.v2.io.LongBuffer;
import com.fineio.v2.io.ShortBuffer;
import com.fineio.v2.io.read.ByteReadBuffer;
import com.fineio.v2.io.read.CharReadBuffer;
import com.fineio.v2.io.read.DoubleReadBuffer;
import com.fineio.v2.io.read.FloatReadBuffer;
import com.fineio.v2.io.read.IntReadBuffer;
import com.fineio.v2.io.read.LongReadBuffer;
import com.fineio.v2.io.read.ShortReadBuffer;
import com.fineio.v2.io.write.ByteWriteBuffer;
import com.fineio.v2.io.write.CharWriteBuffer;
import com.fineio.v2.io.write.DoubleWriteBuffer;
import com.fineio.v2.io.write.FloatWriteBuffer;
import com.fineio.v2.io.write.IntWriteBuffer;
import com.fineio.v2.io.write.LongWriteBuffer;
import com.fineio.v2.io.write.ShortWriteBuffer;

import java.net.URI;

/**
 * Created by daniel on 2017/4/25.
 * 不支持大文件，建议使用小于64M文件
 */
public abstract class DirectIOFile<E extends Buffer> {
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
        ((DoubleWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<ByteBuffer> file, byte d) {
        ((ByteWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<CharBuffer> file, char d) {
        ((CharWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<FloatBuffer> file, float d) {
        ((FloatWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<LongBuffer> file, long d) {
        ((LongWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<IntBuffer> file, int d) {
        ((IntWriteBuffer) file.getBuffer()).put(d);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     *
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<ShortBuffer> file, short d) {
        ((ShortWriteBuffer) file.getBuffer()).put(d);
    }


    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<DoubleBuffer> file, int p, double d) {
        ((DoubleWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<ByteBuffer> file, int p, byte d) {
        ((ByteWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<CharBuffer> file, int p, char d) {
        ((CharWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<FloatBuffer> file, int p, float d) {
        ((FloatWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<LongBuffer> file, int p, long d) {
        ((LongWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<IntBuffer> file, int p, int d) {
        ((IntWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机写
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<ShortBuffer> file, int p, short d) {
        ((ShortWriteBuffer) file.getBuffer()).put(p, d);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static long getLong(DirectIOFile<LongBuffer> file, int p) {
        return ((LongReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static int getInt(DirectIOFile<IntBuffer> file, int p) {
        return ((IntReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static char getChar(DirectIOFile<CharBuffer> file, int p) {
        return ((CharReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static double getDouble(DirectIOFile<DoubleBuffer> file, int p) {
        return ((DoubleReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static float getFloat(DirectIOFile<FloatBuffer> file, int p) {
        return ((FloatReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static byte getByte(DirectIOFile<ByteBuffer> file, int p) {
        return ((ByteReadBuffer) file.getBuffer()).get(p);
    }

    /**
     * 随机读
     *
     * @param file
     * @param p
     * @return
     */
    public final static short getShort(DirectIOFile<ShortBuffer> file, int p) {
        return ((ShortReadBuffer) file.getBuffer()).get(p);
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

    /**
     * 获取文件指定类型的长度
     *
     * @return
     */
    public int length() {
        return getBuffer().getLength();
    }

    /**
     * 获取文件指byte的长度
     *
     * @return
     */
    public int byteLength() {
        return getBuffer().getByteSize();
    }

    protected abstract Buffer initBuffer();

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
}
