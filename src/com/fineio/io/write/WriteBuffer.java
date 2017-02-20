package com.fineio.io.write;

import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.io.Buffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/15.
 * 注意 写是连续的并且不支持并发操作哦
 */
public abstract class WriteBuffer extends Buffer {

    //1024
    private static final int DEFAULT_CAPACITY_OFFSET = 10;

    protected int current_max_size;

    protected int current_max_offset = DEFAULT_CAPACITY_OFFSET;

    protected int max_offset;

    protected int position;

    protected WriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block);
        this.max_offset = max_offset;
        this.max_size = 1 << max_offset;
    }

    private void setCurrentCapacity(int offset) {
        this.current_max_offset = offset;
        this.current_max_size = 1 << offset;
    }


    protected void ensureCapacity(){
        if(position < max_size) {
            if ( position >= current_max_size){
                setCurrentCapacity(this.current_max_offset + 1);
                this.address = MemoryUtils.reallocate(address, this.current_max_size << getLengthOffset());
            }
        } else {
            throw new BufferIndexOutOfBoundsException(position);
        }
    }



}
