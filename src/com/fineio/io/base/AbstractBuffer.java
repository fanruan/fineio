package com.fineio.io.base;

import com.fineio.file.FileBlock;
import com.fineio.storage.Connector;

import java.io.InputStream;

/**
 * Created by daniel on 2017/2/15.
 */
public abstract class AbstractBuffer implements BaseBuffer {

    /**
     * 不再重新赋值
     */
    protected final Connector connector;
    protected final FileBlock block;
    protected volatile long address;
    protected volatile int max_size;


    /**
     * inputStream只有在close的情况下才会允许这边buffer的释放操作，否则会导致jvm崩溃
     * @return
     */
    public final InputStream createInputStream() {
        DirectInputStream inputStream =  new DirectInputStream(address, getByteSize());

        return inputStream;
    }

    protected int getByteSize() {
        return max_size << getLengthOffset();
    }


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
