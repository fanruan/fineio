package com.fineio.file;

import com.fineio.io.read.*;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class ReadIOFile<T extends Read> extends AbstractReadIOFile<T> {

    private ReadIOFile(Connector connector, URI uri, Class<T> clazz){
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
    public static final <E extends Read> ReadIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new ReadIOFile<E>(connector, uri, clazz);
    }

    public final static long getLong(IOFile<LongReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static int getInt(IOFile<IntReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static int getChar(IOFile<CharReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static double getDouble(IOFile<DoubleReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static float getFloat(IOFile<FloatReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static byte getByte(IOFile<ByteReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static short getShort(IOFile<ShortReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }


    public String getPath(){
        return uri.getPath();
    }

}
