package com.fineio.file;

import com.fineio.io.*;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class EditIOFile<T extends Buffer> extends AbstractReadIOFile<T> {

    EditIOFile(Connector connector, URI uri, Class<T> clazz){
        super(connector, uri, clazz);
    }

    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param clazz 子类型
     * @param <E> 继承EditBuffer的子类型
     * @return
     */
    public static final <E extends Buffer> EditIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new EditIOFile<E>(connector, uri, clazz);
    }

}
