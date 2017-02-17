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
    protected int max_byte_len;

    /**
     * max_offset 为什么要作为参数传进来而不是从connector里面读呢 是因为可能我上次写的cube的时候配置的4M 后来改成了64M这样的情况下读取connecter的值就会导致原来的值不对
     * 因为File里面获取到的offset是去掉类型偏移量的值，所以这里的offset需要加上偏移量，纯粹是比较2，不过这里都是不对外公开的，无所谓拉
     * 所以这个max_offset是传进来的，并且是当前文件的offset的值
     * @see com.fineio.file.FineReadIOFile
     * @param connector
     * @param block
     * @param max_offset
     */
    protected Buffer(Connector connector, FileBlock block, int max_offset) {
        this.connector = connector;
        this.block = block;
        this.max_byte_len = 1 << (max_offset + getLengthOffset());
    }

    /**
     *基础类型相对于byte类型的偏移量比如int是4个byte那么便宜量是2
     * long是8个byte那么偏移量是3
     * @return
     */
    protected abstract int getLengthOffset();
}
