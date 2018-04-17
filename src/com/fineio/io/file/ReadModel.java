package com.fineio.io.file;

import com.fineio.io.pool.BufferPool;
import com.fineio.io.pool.PoolMode;
import com.fineio.io.read.ReadBuffer;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/23.
 */
public abstract class ReadModel<T> extends AbstractFileModel<T>{
    protected BufferPool pool = BufferPool.getInstance(getPoolMode());
    public  abstract PoolMode getPoolMode();

    @Override
    protected <F extends T> F createBuffer(Connector connector, FileBlock block, int max_offset) {
        ReadBuffer buffer =  pool.getBuffer(block.getBlockURI());
        if (null == buffer) {
            buffer = (ReadBuffer) newBlockBuffer(connector, block, max_offset);
        }
        return (F) buffer;
    }

    protected abstract <F extends T> F  newBlockBuffer(Connector connector, FileBlock block, int max_offset);
}
