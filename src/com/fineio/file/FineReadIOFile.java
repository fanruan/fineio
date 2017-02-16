package com.fineio.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.read.*;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class FineReadIOFile<T extends ReadBuffer> extends FineIOFile<T> {

    private long single_block_len;
    private T[] buffers;
    private Class<T> parameterClazz;

    private FineReadIOFile(Connector connector, URI uri,  Class<T> clazz){
        super(connector, uri);
        byte offset = initParameterClazz(clazz);
        readHeader(offset);

    }

    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param clazz 子类型
     * @param <E> 继承ReadBuffer的子类型
     * @return
     */
    public static final <E extends ReadBuffer> FineReadIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new FineReadIOFile<E>(connector, uri, clazz);
    }

    private byte initParameterClazz(Class<T> clazz) {
        parameterClazz = clazz;
        try {
            Field field = parameterClazz.getDeclaredField(FileConstants.OFFSET_FIELD_NAME);
            return ((byte) ((Integer)field.get(null)).intValue());
        } catch (Exception e) {
            return ByteReadBuffer.OFFSET;
        }
    }

    private int checkIndex(int index){
        if(index > -1 && index < blocks){
            return index;
        }
        throw new BufferIndexOutOfBoundsException(index);
    }

    private T getBuffer(int index){
        return buffers[checkIndex(index)] != null ? buffers[index] : initBuffer(index);
    }

    private T initBuffer(int index) {
        synchronized (this){
            if(buffers[index] == null) {
                buffers[index] = createBuffer(parameterClazz, index);
            }
            return buffers[index];
        }
    }

    private int gi(long p) {
        return (int)(p >> block_size_offset);
    }

    private int gp(long p){
        return (int)(p & single_block_len);
    }

    public static long getLong(FineReadIOFile<LongReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public static int getInt(FineReadIOFile<IntReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public static int getChar(FineReadIOFile<CharReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public static double getDouble(FineReadIOFile<DoubleReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public static float getFloat(FineReadIOFile<FloatReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public static byte getByte(FineReadIOFile<ByteReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public static short getShort(FineReadIOFile<ShortReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public static long getLong(FineIOFile<LongReadBuffer> file, long p) {
        return getLong((FineReadIOFile<LongReadBuffer>)file, p);
    }

    public static int getInt(FineIOFile<IntReadBuffer> file, long p) {
        return getInt((FineReadIOFile<IntReadBuffer>)file, p);
    }

    public static int getChar(FineIOFile<CharReadBuffer> file, long p) {
        return getChar((FineReadIOFile<CharReadBuffer>)file, p);
    }

    public static double getDouble(FineIOFile<DoubleReadBuffer> file, long p) {
        return getDouble((FineReadIOFile<DoubleReadBuffer>)file, p);
    }

    public static float getFloat(FineIOFile<FloatReadBuffer> file, long p) {
        return getFloat((FineReadIOFile<FloatReadBuffer>)file, p);
    }

    public static byte getByte(FineIOFile<ByteReadBuffer> file, long p) {
        return getByte((FineReadIOFile<ByteReadBuffer>)file, p);
    }

    public static short getShort(FineIOFile<ShortReadBuffer> file, long p) {
        return getShort((FineReadIOFile<ShortReadBuffer>)file, p);
    }



    private void readHeader(byte offset) {
        InputStream is  = this.connector.read(new FileBlock(uri, FileConstants.HEAD));
        if(is == null){
            throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
        }
        try {
            byte[] header = new byte[9];
            is.read(header);
            int p = 0;
            blocks = Bits.getInt(header, p);
            buffers = (T[])new ReadBuffer[blocks];
            //先空个long的位置
            p += MemoryConstants.STEP_LONG;
            block_size_offset = (byte) (header[p] - offset);
            single_block_len = (1L << block_size_offset) - 1;
        } catch (IOException e) {
            throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
        }
    }

    public String getPath(){
        return uri.getPath();
    }

}
