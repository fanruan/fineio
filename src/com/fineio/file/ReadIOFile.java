package com.fineio.file;

import com.fineio.io.*;
import com.fineio.io.read.*;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class ReadIOFile<T extends Buffer> extends AbstractReadIOFile<T> {

    private ReadIOFile(Connector connector, URI uri, Class<T> clazz){
        super(connector, uri, clazz);
    }

    @Override
    protected Class<T> getBufferClass(Class<T> clazz) {
        if(clazz == ByteBuffer.class){
            return (Class<T>) ByteReadBuffer.class;
        }
        return clazz;
    }

    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param clazz 子类型
     * @param <E> 继承ReadBuffer的子类型
     * @return
     */
    public static final <E extends Buffer> ReadIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new ReadIOFile<E>(connector, uri, clazz);
    }



    public String getPath(){
        return uri.getPath();
    }

}
