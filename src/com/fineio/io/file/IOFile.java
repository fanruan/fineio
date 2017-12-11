package com.fineio.io.file;

import com.fineio.base.Bits;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.IOSetException;
import com.fineio.io.*;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 *
 */
public abstract class IOFile<E extends Buffer> {


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
    private AbstractFileModel<E> model;
    /**
     * 单个block的大小
     */
    protected long single_block_len;
    protected volatile E[] buffers;

    private volatile boolean released = false;

    /*
    * 文件长度
     */
//    public abstract long length();

    IOFile(Connector connector, URI uri, AbstractFileModel<E> model) {
        if(uri == null || connector == null|| model == null){
            throw new IOSetException("uri  or connector or model can't be null");
        }
        this.connector = connector;
        this.uri = URI.create(uri.getPath() + "/");
        this.model = model;
    }

    /**
     * 获取设置的path
     */
    public String getPath(){
        return uri.getPath();
    }

    protected final FileBlock createHeadBlock(){
        return new FileBlock(uri, FileConstants.HEAD);
    }

    /**
     * 注意所有写方法并不支持多线程操作，仅读的方法支持
     * @param size
     */
    protected final void createBufferArray(int size) {
        this.blocks = size;
        this.buffers = (E[]) new Buffer[size];
    }

    private boolean inRange(int index) {
        return buffers != null && buffers.length > index;
    }

    protected final int checkBuffer(int index) {
        if(index < 0){
            throw new BufferIndexOutOfBoundsException(index);
        }
        return inRange(index) ? index : createBufferArrayInRange(index);
    }

    private int createBufferArrayInRange(int index) {
        Buffer[] buffers = this.buffers;
        createBufferArray(index + 1);
        if(buffers != null){
            System.arraycopy(buffers, 0, this.buffers, 0, buffers.length);
        }
        return index;
    }

    //读
    protected  int gi(long p) {
        return   (int)(p >> block_size_offset);
    }

    //触发写
    protected int giw(long p) {
        int len =  (int)(p >> block_size_offset);
        if(len > 0){
            checkWrite(len);
        }
        return len;
    }

    protected   void checkWrite(int len) {
        if(bufferWriteIndex != len) {
            if(bufferWriteIndex != -1){
                buffers[bufferWriteIndex].write();
            }
            bufferWriteIndex = len;
        }
    }

    private volatile int bufferWriteIndex = -1;

    private final int gi() {
        if(buffers == null || buffers.length == 0) {
            return 0;
        }
        int len = buffers.length - 1;
        return buffers[len].full() ? triggerWrite(len + 1) : len;
    }

    private final int triggerWrite(int len) {
        checkWrite(len);
        return len;
    }



    private final int gp(long p){
        return (int)(p & single_block_len);
    }


    private final E getBuffer(int index){
        return buffers[checkIndex(index)] != null ?  buffers[index] : initBuffer(index);
    }

    private int checkIndex(int index){
        if(index > -1 && index < blocks){
            return index;
        }
        throw new BufferIndexOutOfBoundsException(index);
    }

    private E initBuffer(int index) {
        synchronized (this){
            if(buffers[index] == null) {
                buffers[index] = createBuffer(index);
            }
            return buffers[index];
        }
    }


    /**
     * 删除操作
     * @return
     */
    public boolean delete(){
        synchronized (this) {
            boolean delete = connector.delete(createHeadBlock());
            if (buffers != null) {
                for (int i = 0; i < buffers.length; i++) {
                    //内存泄露
                    if (!released && buffers[i] != null) {
                        buffers[i].closeWithOutSync();
                        buffers[i] = null;
                    }
                    boolean v = connector.delete(createIndexBlock(i));
                    if (delete) {
                        delete = v;
                    }
                }
            }
            boolean v = connector.delete(new FileBlock(uri));
            if (delete) {
                delete = v;
            }
            URI parentURI = uri;
            while (null != (parentURI = connector.deleteParent(new FileBlock(parentURI))));
            released = true;
            return  delete;
        }
    }
    /**
     * 复制
     * @return
     */
    public  boolean copyTo(URI destUri){
        synchronized(this) {
            try {
                if (buffers != null) {
                    URI destURI = URI.create(destUri.getPath() + "/");
                    connector.copy(createHeadBlock(), new FileBlock(destURI, FileConstants.HEAD));
                    for (int i = 0; i < buffers.length; i++) {
                        connector.copy(createIndexBlock(i), new FileBlock(destURI, String.valueOf(i)));
                    }
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * 判断是否存在
     * @return
     */
    public boolean exists() {
        synchronized (this) {
            boolean exists = connector.exists(createHeadBlock());
//            boolean v = connector.exists(createIndexBlock(0));
//            if (exists) {
//                exists = v;
//            }
            released = true;
            return  exists;
        }
    }

    private final FileBlock createIndexBlock(int index){
        return new FileBlock(uri, String.valueOf(index));
    }


    private E createBuffer(int index) {
        return model.createBuffer(connector, createIndexBlock(index), block_size_offset);
    }

    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<DoubleBuffer> file, double d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<ByteBuffer> file,   byte d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<CharBuffer> file,  char d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(  d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<FloatBuffer> file,  float d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(  d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<LongBuffer> file,   long d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<IntBuffer> file, int d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(  d);
    }
    /**
     * 连续写的方法，从当前已知的最大位置开始写
     * @param file
     * @param d
     */

    public static void put(IOFile<ShortBuffer> file,  short d) {
        file.getBuffer(file.checkBuffer(file.gi( ))).put(  d);
    }


    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<DoubleBuffer> file, long p, double d) {
        file.getBuffer(file.checkBuffer(file.giw(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<ByteBuffer> file, long p, byte d) {
        file.getBuffer(file.checkBuffer(file.giw(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<CharBuffer> file, long p, char d) {
        file.getBuffer(file.checkBuffer(file.giw(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<FloatBuffer> file, long p, float d) {
        file.getBuffer(file.checkBuffer(file.giw(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<LongBuffer> file, long p, long d) {
        file.getBuffer(file.checkBuffer(file.giw(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<IntBuffer> file, long p, int d) {
        file.getBuffer(file.checkBuffer(file.giw(p))).put(file.gp(p), d);
    }
    /**
     * 随机写
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<ShortBuffer> file, long p, short d) {
        file.getBuffer(file.checkBuffer(file.giw(p))).put(file.gp(p), d);
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static long getLong(IOFile<LongBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static int getInt(IOFile<IntBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static char getChar(IOFile<CharBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static double getDouble(IOFile<DoubleBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static float getFloat(IOFile<FloatBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static byte getByte(IOFile<ByteBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }
    /**
     * 随机读
     * @param file
     * @param p
     * @return
     */
    public final static short getShort(IOFile<ShortBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    private final static int HEAD_LEN = MemoryConstants.STEP_LONG + 1;

    protected void writeHeader() {
        FileBlock block = createHeadBlock();
        byte[] bytes = new byte[HEAD_LEN];
        Bits.putInt(bytes, 0, buffers == null ? 0 : buffers.length);
        bytes[MemoryConstants.STEP_LONG] = (byte) (block_size_offset + model.offset());
        try {
            connector.write(block, bytes);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void finalize() {
        //防止没有执行close导致内存泄露
        close();
    }

    public void close() {
        synchronized (this) {
            if(released){
                return;
            }
            writeHeader();
            if(buffers != null) {
                for (int i = 0; i < buffers.length; i++) {
                    if (buffers[i] != null) {
                        buffers[i].force();
                        buffers[i] = null;
                    }
                }
            }
            released = true;
        }
    }
}
