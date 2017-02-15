package com.fineio.io;

import com.fineio.file.FileBlock;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/15.
 */
public abstract class Buffer {

    /**
     * 不再重新赋值
     */
    protected final Connector connector;
    protected final FileBlock block;
    protected volatile long address;

    protected Buffer(Connector connector, FileBlock block) {
        this.connector = connector;
        this.block = block;
    }

}
