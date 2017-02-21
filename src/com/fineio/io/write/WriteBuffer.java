package com.fineio.io.write;

import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.io.AbstractBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/15.
 * 注意 写是连续的并且不支持并发操作哦
 */
public abstract class WriteBuffer extends AbstractBuffer implements Write {

    //1024
    public static final int DEFAULT_CAPACITY_OFFSET = 10;

    protected int current_max_size;

    protected int current_max_offset = DEFAULT_CAPACITY_OFFSET;

    protected int max_offset;

    protected int max_position = 0;

    protected WriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block);
        this.max_offset = max_offset;
        this.max_size = 1 << max_offset;
    }

    protected void setCurrentCapacity(int offset) {
        this.current_max_offset = offset;
        this.current_max_size = 1 << offset;
    }


    protected void ensureCapacity(int position){
        if(position < max_size) {
            while ( position >= current_max_size){
                setCurrentCapacity(this.current_max_offset + 1);
                //TODO memory control
                this.address = MemoryUtils.reallocate(address, this.current_max_size << getLengthOffset());
            }
            if(position > max_position){
                max_position = position;
            }
        } else {
            throw new BufferIndexOutOfBoundsException(position);
        }
    }



}
