package com.fineio.file;

import com.fineio.exception.*;
import com.fineio.io.AbstractBuffer;
import com.fineio.io.Buffer;
import com.fineio.io.read.ByteReadBuffer;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
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
    protected Class<E> parameterClazz;
    /**
     * 单个block的大小
     */
    protected long single_block_len;
    protected E[] buffers;


    IOFile(Connector connector, URI uri, Class<E> clazz) {
        if(uri == null || connector == null){
            throw new IOSetException("uri  or connector can't be null");
        }
        this.connector = connector;
        this.uri = uri;
        parameterClazz = clazz;
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

    protected final int gi(long p) {
        return (int)(p >> block_size_offset);
    }

    protected final int gp(long p){
        return (int)(p & single_block_len);
    }

    protected byte getOffset() {
        try {
            Field field = parameterClazz.getInterfaces()[0].getDeclaredField(FileConstants.OFFSET_FIELD_NAME);
            return ((byte) ((Integer)field.get(null)).intValue());
        } catch (Exception e) {
            throw new ClassDefException(e);
        }
    }

    protected final E getBuffer(int index){
        return buffers[checkIndex(index)] != null ? buffers[index] : initBuffer(index);
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
                buffers[index] = createBuffer(parameterClazz, index);
            }
            return buffers[index];
        }
    }


    protected  <T extends E> T createBuffer(Class<T> clazz, int index) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(Connector.class, FileBlock.class, int.class);
            constructor.setAccessible(true);
            return constructor.newInstance(connector, new FileBlock(uri, String.valueOf(index)), block_size_offset);
        } catch (Exception e) {
            throw new BufferConstructException(e);
        }
    }

}
