package com.fineio.io;

import com.fineio.file.FileBlock;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/15.
 */
public abstract class AbstractBuffer implements Buffer{

    /**
     * 不再重新赋值
     */
    protected final Connector connector;
    protected final FileBlock block;
    protected volatile long address;
    protected volatile int max_size;

    protected AbstractBuffer(Connector connector, FileBlock block) {
        this.connector = connector;
        this.block = block;
    }

    /**
     *基础类型相对于byte类型的偏移量比如int是4个byte那么便宜量是2
     * long是8个byte那么偏移量是3
     * 不放在接口里面是因为protected类型
     * @return
     */
    protected abstract int getLengthOffset();
}
