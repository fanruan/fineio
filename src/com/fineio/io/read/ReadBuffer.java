package com.fineio.io.read;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.io.Buffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daniel on 2017/2/9.
 */
public abstract class ReadBuffer extends Buffer {
    protected volatile int byteLen;
    protected volatile int max_size;
    private volatile boolean load = false;
    private int max_byte_len;


    protected ReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block);
        this.max_byte_len = 1 << (max_offset + getLengthOffset());
    }


    private final  void loadData(){
        synchronized (this) {
            if (load) {
                return;
            }
            InputStream is = connector.read(block);
            if (is == null) {
                throw new BlockNotFoundException("block:" + block.toString() + " not found!");
            }
            try {
                byte[] bytes = new byte[max_byte_len];
                byteLen = is.read(bytes);
                address = MemoryUtils.allocate(byteLen);
                MemoryUtils.copyMemory(bytes, address, byteLen);
                load = true;
                max_size = byteLen >> getLengthOffset();
            } catch (IOException e) {
                throw new BlockNotFoundException("block:" + block.toString() + " not found!");
            }
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
            ll(p);
        }
    }

    private void ll(int p) {
        loadData();
        checkIndex(p);
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
