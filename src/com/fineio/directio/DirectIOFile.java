package com.fineio.directio;

import com.fineio.io.*;
import com.fineio.io.file.AbstractFileModel;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/4/25.
 * 不支持大文件，建议使用小于64M文件
 */
public abstract class DirectIOFile<E extends Buffer> {
    protected Connector connector;
    protected URI uri;
    protected volatile E buffer;

    private AbstractFileModel<E> model;

    protected DirectIOFile(Connector connector, URI uri,  AbstractFileModel<E> model){
        this.connector = connector;
        this.uri = uri;
        this.model = model;
    }

    /**
     * 获取设置的path
     */
    public String getPath(){
        return uri.getPath();
    }


    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<DoubleBuffer> file, double d) {
        file.getBuffer().put(d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<ByteBuffer> file, byte d) {
        file.getBuffer().put(d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<CharBuffer> file, char d) {
        file.getBuffer().put(  d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<FloatBuffer> file, float d) {
        file.getBuffer().put(  d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<LongBuffer> file,   long d) {
        file.getBuffer().put(d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<IntBuffer> file, int d) {
        file.getBuffer().put(  d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(DirectIOFile<ShortBuffer> file,  short d) {
        file.getBuffer().put(  d);
    }


    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<DoubleBuffer> file, int p, double d) {
        file.getBuffer().put(p, d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<ByteBuffer> file, int p, byte d) {
        file.getBuffer().put(p, d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<CharBuffer> file, int p, char d) {
        file.getBuffer().put(p, d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<FloatBuffer> file, int p, float d) {
        file.getBuffer().put(p, d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<LongBuffer> file, int p, long d) {
        file.getBuffer().put(p, d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<IntBuffer> file, int p, int d) {
        file.getBuffer().put(p, d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<ShortBuffer> file, int p, short d) {
        file.getBuffer().put(p, d);
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static long getLong(DirectIOFile<LongBuffer> file, int p) {
        return file.getBuffer().get(p);
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static int getInt(DirectIOFile<IntBuffer> file, int p) {
        return file.getBuffer().get(p);
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static char getChar(DirectIOFile<CharBuffer> file, int p) {
        return file.getBuffer().get(p);
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static double getDouble(DirectIOFile<DoubleBuffer> file, int p) {
        return file.getBuffer().get(p);
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static float getFloat(DirectIOFile<FloatBuffer> file, int p) {
        return file.getBuffer().get(p);
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static byte getByte(DirectIOFile<ByteBuffer> file, int p) {
        return file.getBuffer().get(p);
    }
    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static short getShort(DirectIOFile<ShortBuffer> file, long p) {
        return file.getBuffer().get((int) p);
    }


    private E getBuffer() {
        return buffer != null? buffer : initBuffer();
    }

    /**
     * 获取文件指定类型的长度
     * @return
     */
    public int length() {
        return getBuffer().getLength();
    }


    /**
     * 获取文件指byte的长度
     * @return
     */
    public int byteLength() {
        return getBuffer().getByteSize();
    }

    protected E initBuffer() {
        if(buffer == null){
            synchronized (this) {
                buffer = model.createBuffer(connector, uri);
            }
        }
        return  buffer;
    }

    protected void finalize() {
        //防止没有执行close导致内存泄露
        close();
    }

    private volatile boolean released = false;

    public void close() {
        synchronized (this) {
            if(released){
                return;
            }
            if(buffer != null) {
                buffer.force();
            }
            released = true;
        }
    }
}
