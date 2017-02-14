package com.fineio.io.read;

import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/9.
 */
public class ByteReadBuffer {
    public volatile long address;
    protected volatile int byteLen;
    protected volatile int max_size;
    private Connector connector;
    private FileBlock block;
    private volatile boolean load = false;

    public ByteReadBuffer(Connector connector, FileBlock block) {
        this.connector = connector;
        this.block = block;
    }


    private final synchronized void loadData(){
        if(load){
            return;
        }
        byte[] bytes = connector.read(block);
        if(bytes == null){
            throw new BlockNotFoundException("block:" + block.toString() +" not found!");
        }
        byteLen = bytes.length;
        max_size = byteLen >> getLengthOffset();
        address = MemoryUtils.allocate(bytes.length);
        long i = 0;
        for(byte b : bytes){
            MemoryUtils.put(address, i++, b);
        }
        load = true;
    }

    protected int getLengthOffset() {
        return MemoryConstants.OFFSET_BYTE;
    }


    protected final void checkIndex(int p) {
        if (p > -1 && p < max_size){
            return;
        }
        lc(p);
    }

    private final void lc(int p) {
        if(load){
            throw new BufferIndexOutOfBoundsException(p);
        } else {
            loadData();
            checkIndex(p);
        }
    }

    public final byte get(int p){
        checkIndex(p);
        return MemoryUtils.getByte(address, p);
    }

    public final synchronized void clear() {
        if(!load){
            return;
        }
        max_size = 0;
        load = false;
        //释放方法要给时间允许get返回
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }
        MemoryUtils.free(address);
    }
}
