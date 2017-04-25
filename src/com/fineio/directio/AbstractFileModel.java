package com.fineio.io.file;

import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/23.
 * 这里不用接口是为了访问受限
 */
public abstract class AbstractFileModel<T> {

        protected abstract <F extends T>  F createBuffer(Connector connector, FileBlock block, int max_offset);

        protected abstract byte offset();

}
