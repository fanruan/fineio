package com.fineio.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.read.*;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class FineReadIOFile<T extends Read> extends FineAbstractReadFile<T> {

    private FineReadIOFile(Connector connector, URI uri,  Class<T> clazz){
        super(connector, uri, clazz);
    }

    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param clazz 子类型
     * @param <E> 继承ReadBuffer的子类型
     * @return
     */
    public static final <E extends Read> FineReadIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new FineReadIOFile<E>(connector, uri, clazz);
    }

    public final static long getLong(FineIOFile<LongReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static int getInt(FineIOFile<IntReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static int getChar(FineIOFile<CharReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static double getDouble(FineIOFile<DoubleReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static float getFloat(FineIOFile<FloatReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static byte getByte(FineIOFile<ByteReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static short getShort(FineIOFile<ShortReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }


    public String getPath(){
        return uri.getPath();
    }

}
