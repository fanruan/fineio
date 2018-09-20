package com.fineio.v2.io;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/6/1
 */
public abstract class BufferModel<T> {
    /**
     * @param connector
     * @param block
     * @param max_offset
     * @param <F>
     * @return
     */
    abstract <F extends T> F createBuffer(Connector connector, FileBlock block, int max_offset);

    /**
     * 直接访问的创建接口
     *
     * @param connector
     * @param uri
     * @param <F>
     * @return
     */
    abstract <F extends T> F createBuffer(Connector connector, URI uri);


    /**
     * 便宜值
     *
     * @return
     */
    abstract byte offset();
}
