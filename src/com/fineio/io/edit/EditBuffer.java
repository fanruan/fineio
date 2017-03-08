package com.fineio.io.edit;

import com.fineio.base.Maths;
import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.FileCloseException;
import com.fineio.io.file.FileBlock;
import com.fineio.io.write.WriteBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.InputStream;

/**
 * Created by daniel on 2017/2/20.
 *  写入方法均不支持多线程
 */
public abstract class EditBuffer extends WriteBuffer implements Edit {

    private volatile boolean load = false;


    protected EditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private final  void loadData(){
        synchronized (this) {
            if (load) {
                return;
            }
            if(close) {
                throw  new FileCloseException();
            }

            int max_byte_len = max_size << getLengthOffset();
            byte[] bytes = new byte[max_byte_len];
            int off = 0;
            int len = 0;
            try {
                InputStream is = bufferKey.getConnector().read(bufferKey.getBlock());
                while ((len = is.read(bytes, off, max_byte_len - off)) > 0) {
                    off += len;
                }
            } catch (Throwable e) {
                //文件不存在新建一个不loaddata了
            }
            int max_position = off >> getLengthOffset();
            int offset = Maths.log2(max_position);
            if(max_position > (1 << offset)){
                offset++;
            }
            len = 1 << offset << getLengthOffset();
            beforeStatusChange();
            try {
                address = CacheManager.getInstance().allocateRead(len);
                MemoryUtils.copyMemory(bytes, address, off);
                MemoryUtils.fill0(address + off, len - off);
            } catch (OutOfMemoryError error){
                //todo 预防内存设置超大 赋值的时候发生溢出需要抛出异常
            }
            load = true;
            this.max_position = max_position;
            setCurrentCapacity(offset);
            afterStatusChange();
        }
    }

    protected void ensureCapacity(int position){
        if(!load) {
            loadData();
        }
        if(position < max_size) {
            addCapacity(position);
        } else {
            throw new BufferIndexOutOfBoundsException(position);
        }
    }

    protected final void checkIndex(int p) {
        if (ir(p)){
            access();
            return;
        }
        lc(p);
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

    protected void write0(){
        synchronized (this) {
            changed = false;
            bufferKey.getConnector().write(bufferKey.getBlock(), getInputStream());
            flushed = true;
        }
    }


    /**
     * force关闭load入口不在加载
     */
    public void force() {
        super.force();
        synchronized (this) {
            if (!load) {
                return;
            }
            load = false;
            super.closeDuringClear();
            super.clear();
        }
    }

    public LEVEL getLevel() {
        return LEVEL.EDIT;
    }

    /**
     * clear并不关闭 force才会关闭
     */
    protected void closeDuringClear(){
        //do nothing
    }

    /**
     * clear仅仅是clear而已，如果另个线程在写。clear是clear不掉的
     */
    public void clear(){
        super.force();
        synchronized (this) {
            if (!load) {
                return;
            }
            load = false;
            super.clear();
        }
    }
}
