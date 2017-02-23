package com.fineio.file;

import com.fineio.io.*;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class WriteIOFile<T extends Buffer> extends IOFile<T> {

    WriteIOFile(Connector connector, URI uri, Class<T> clazz){
        super(connector, uri, clazz);
        this.block_size_offset = (byte) (connector.getBlockOffset() - getOffset());
        single_block_len = (1L << block_size_offset) - 1;
    }

    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param clazz 子类型
     * @param <E> 继承ReadBuffer的子类型
     * @return
     */
    public static final <E extends Buffer> WriteIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new WriteIOFile<E>(connector, uri, clazz);
    }


}
