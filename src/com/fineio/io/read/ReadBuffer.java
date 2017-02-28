package com.fineio.io.read;

import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.FileCloseException;
import com.fineio.file.FileBlock;
import com.fineio.file.ReadIOFile;
import com.fineio.io.base.AbstractBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daniel on 2017/2/9.
 */
public abstract class ReadBuffer extends AbstractBuffer implements Read {
    private volatile boolean load = false;
    protected int max_byte_len;
    private boolean close = false;


    public void put(int position, byte b) {
        put(b);
    }

    public void put(int position, int b) {
        put(b);
    }

    public void put(int position, double b) {
        put(b);
    }

    public void put(int position, long b) {
        put(b);
    }

    public void put(int position, char b) {
        put(b);
    }

    public void put(int position, short b) {
        put(b);
    }

    public void put(int position, float b) {
        put(b);
    }


    public void put(byte b) {
        unSupport();
    }

    private void unSupport() {
        throw new UnsupportedOperationException(this.getClass().getName() + " put");
    }

    public void put(int b) {
        unSupport();
    }

    public void put(double b) {
        unSupport();
    }

    public void put(long b) {
        unSupport();
    }

    public void put(char b) {
        unSupport();
    }

    public void put(short b) {
        unSupport();
    }

    public void put(float b) {
        unSupport();
    }

    public void write() {
        unSupport();
    }

    /**
     * max_offset 为什么要作为参数传进来而不是从connector里面读呢 是因为可能我上次写的cube的时候配置的4M 后来改成了64M这样的情况下读取connecter的值就会导致原来的值不对
     * 因为File里面获取到的offset是去掉类型偏移量的值，所以这里的offset需要加上偏移量，纯粹是比较2，不过这里都是不对外公开的，无所谓拉
     * 所以这个max_offset是传进来的，并且是当前文件的offset的值
     * @see ReadIOFile
     * @param connector
     * @param block
     * @param max_offset
     */
    protected ReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block);
        this.max_byte_len = 1 << (max_offset + getLengthOffset());
    }


    private final  void loadData(){
        synchronized (this) {
            if (load) {
                return;
            }
            if(close) {
                throw new FileCloseException();
            }
            InputStream is = connector.read(block);
            if (is == null) {
                throw new BlockNotFoundException("block:" + block.toString() + " not found!");
            }
            try {
                byte[] bytes = new byte[max_byte_len];
                int off = 0;
                int len = 0;
                while ((len = is.read(bytes, off, max_byte_len - off)) > 0) {
                    off+=len;
                }
                beforeStatusChange();
                //TODO cache部分要做内存限制等处理  还有预加载线程
                address = MemoryUtils.allocate(off);
                MemoryUtils.copyMemory(bytes, address, off);
                load = true;
                max_size = off >> getLengthOffset();
                afterStatusChange();
            } catch (IOException e) {
                throw new BlockNotFoundException("block:" + block.toString() + " not found!");
            }
        }
    }

    public boolean full() {
        return (max_byte_len >> getLengthOffset()) == max_size;
    }

    protected final void checkIndex(int p) {
        if (ir(p)){
            return;
        }
        lc(p);
    }

    private final boolean ir(int p){
        return p > -1 && p < max_size;
    }

    private final void lc(int p) {
        synchronized (this) {
            if (load) {
                if (ir(p)){
                    return;
                }
                throw new BufferIndexOutOfBoundsException(p);
            } else {
                ll(p);
            }
        }
    }

    private final void ll(int p) {
        loadData();
        checkIndex(p);
    }

    public void clear() {
        synchronized (this) {
            if (!load) {
                return;
            }
            load = false;
            max_size = 0;
            super.clear();
        }
    }

    public void force() {
        close = true;
        clear();
    }
}
