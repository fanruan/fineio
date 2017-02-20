package com.fineio.file;

import com.fineio.exception.BufferConstructException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.Buffer;
import com.fineio.io.read.ByteReadBuffer;
import com.fineio.storage.Connector;
import com.fineio.exception.IOSetException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public abstract class FineIOFile<E extends Buffer> {

    protected URI uri;
    protected Connector connector;
    protected int blocks;
    protected byte block_size_offset;
    protected Class<E> parameterClazz;
    protected long single_block_len;
    protected E[] buffers;


    FineIOFile(Connector connector, URI uri, Class<E> clazz) {
        if(uri == null || connector == null){
            throw new IOSetException("uri  or connector can't be null");
        }
        this.connector = connector;
        this.uri = uri;
        parameterClazz = clazz;
    }


    protected final int gi(long p) {
        return (int)(p >> block_size_offset);
    }

    protected final int gp(long p){
        return (int)(p & single_block_len);
    }

    protected byte getOffset() {
        try {
            Field field = parameterClazz.getDeclaredField(FileConstants.OFFSET_FIELD_NAME);
            return ((byte) ((Integer)field.get(null)).intValue());
        } catch (Exception e) {
            return ByteReadBuffer.OFFSET;
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
