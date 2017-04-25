package com.fineio.io.edit;

import com.fineio.base.Maths;
import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.FileCloseException;
import com.fineio.io.Buffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.write.WriteBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by daniel on 2017/2/20.
 *  写入方法均不支持多线程
 */
public abstract class EditBuffer extends WriteBuffer implements Edit {

    private volatile boolean load = false;


    protected EditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    protected EditBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    private final  void loadData(){
        synchronized (this) {
            if (load) {
                return;
            }
            if(close) {
                throw  new FileCloseException();
            }
            Accessor accessor = null;
            if(directAccess) {
                accessor = new DirectAccessor();
            } else {
                accessor = new VirtualAccessor();
            }
            accessor.invoke();
            int off = accessor.getOff();
            byte[] bytes = accessor.getBytes();
            int max_position = off >> getLengthOffset();
            int offset = Maths.log2(max_position);
            if(max_position > (1 << offset)){
                offset++;
            }
            int len = 1 << offset << getLengthOffset();
            beforeStatusChange();
            try {
                address = CacheManager.getInstance().allocateRead((Buffer) this, len);
                allocateSize = len;
                MemoryUtils.copyMemory(bytes, address, off);
                MemoryUtils.fill0(address + off, len - off);
            } catch (OutOfMemoryError error){
                //todo 预防内存设置超大 赋值的时候发生溢出需要抛出异常
                error.printStackTrace();
            }
            load = true;
            this.max_position = max_position;
            setCurrentCapacity(offset);
            afterStatusChange();
        }
    }


    protected void addCapacity() {
        int len = this.current_max_size << getLengthOffset();
        setCurrentCapacity(this.current_max_offset + 1);
        int newLen = this.current_max_size << getLengthOffset();
        beforeStatusChange();
        try {
            this.address = CacheManager.getInstance().allocateEdit((Buffer) this, address, len, newLen);
            allocateSize = newLen;
            MemoryUtils.fill0(this.address + len, newLen - len);

        } catch (OutOfMemoryError error){
            error.printStackTrace();
        }
        afterStatusChange();
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

    /**
     * force关闭load入口不在加载,force与写也不支持多线程
     */
    public void force() {
        forceWrite();
        synchronized (this) {
            if(close){
                return;
            }
            close();
            cleanMemory();
            releaseBuffer();
        }
    }

    public LEVEL getLevel() {
        return LEVEL.EDIT;
    }


    /**
     * clear仅仅是clear而已，如果另个线程在写。clear是clear不掉的
     */
    public void clear(){
        forceWrite();
        clearAfterWrite();
    }

    private void cleanMemory(){
        synchronized (this) {
            if (!load) {
                return;
            }
            load = false;
            this.current_max_size = 0;
            clearMemory();
        }
    }

    protected void clearAfterWrite() {
        synchronized (this) {
            if (!load) {
                releaseBuffer();
                return;
            }
            load = false;
            //等待1微秒让写的写完
            LockSupport.parkNanos(1000);
            //如果被写入值了 返回把
            if(needFlush()){
                //状态还是load
                load = true;
                return;
            }
            this.current_max_size = 0;
            clearMemory();
            releaseBuffer();
        }
    }

    private abstract class Accessor {
        protected byte[] bytes;
        protected int off;

        public byte[] getBytes() {
            return bytes;
        }

        public int getOff() {
            return off;
        }

        public abstract void invoke();
    }

    private class VirtualAccessor extends Accessor {



        public void invoke() {
            int max_byte_len = max_size << getLengthOffset();
            bytes = new byte[max_byte_len];
            off = 0;
            int len = 0;
            InputStream is = null;
            try {
                is = bufferKey.getConnector().read(bufferKey.getBlock());
                while ((len = is.read(bytes, off, max_byte_len - off)) > 0) {
                    off += len;
                }
            } catch (Throwable e) {
                //文件不存在新建一个不loaddata了
            } finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }


    private class DirectAccessor extends Accessor {



        public void invoke() {
            byte[] b = new byte[1024];
            int len = 0;
            InputStream is = null;
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            try {
                is = bufferKey.getConnector().read(bufferKey.getBlock());
                while ((len = is.read(b, 0, b.length)) > 0) {
                    ba.write(b, 0, len);
                }
                bytes = ba.toByteArray();
                off = bytes.length;
            } catch (Throwable e) {
                //文件不存在新建一个不loaddata了
            } finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}
