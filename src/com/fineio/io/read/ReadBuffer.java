package com.fineio.io.read;

import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.io.Buffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/9.
 */
public abstract class ReadBuffer extends Buffer {
    protected volatile int byteLen;
    protected volatile int max_size;
    private volatile boolean load = false;

    protected ReadBuffer(Connector connector, FileBlock block) {
        super(connector, block);
    }


    private final  void loadData(){
        synchronized (this) {
            if (load) {
                return;
            }
            byte[] bytes = connector.read(block);
            if (bytes == null) {
                throw new BlockNotFoundException("block:" + block.toString() + " not found!");
            }
            byteLen = bytes.length;
            address = MemoryUtils.allocate(bytes.length);
            long i = 0;
            for (byte b : bytes) {
                MemoryUtils.put(address, i++, b);
            }
            load = true;
            max_size = byteLen >> getLengthOffset();
        }
    }

    /**
     *基础类型相对于byte类型的偏移量比如int是4个byte那么便宜量是2
     * long是8个byte那么偏移量是3
     * @return
     */
    protected abstract int getLengthOffset();

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

    public synchronized void clear() {
        synchronized (this) {
            if (!load) {
                return;
            }
            load = false;
            max_size = 0;
            //释放方法要给时间允许get返回
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
            MemoryUtils.free(address);
        }
    }
}
