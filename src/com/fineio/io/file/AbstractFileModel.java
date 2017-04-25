package com.fineio.io.file;

import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/23.
 * 这里不用接口是为了访问受限
 */
public abstract class AbstractFileModel<T> {

        /*
        虚拟文件分块的创建接口
         */
        protected abstract <F extends T>  F createBuffer(Connector connector, FileBlock block, int max_offset);

        /**
         * 直接访问的创建接口
         * @param connector
         * @param uri
         * @param <F>
         * @return
         */
        public abstract <F extends T>  F createBuffer(Connector connector, URI uri);


        /**
         * 便宜值
         * @return
         */
        protected abstract byte offset();

}
