package com.fineio.v1.io.write;

import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.StreamCloseException;
import com.fineio.io.base.Job;
import com.fineio.io.base.JobAssist;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.writer.SyncManager;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;
import com.fineio.v1.cache.CacheManager;
import com.fineio.v1.io.Buffer;
import com.fineio.v1.io.Level;
import com.fineio.v1.io.base.AbstractBuffer;

import java.io.IOException;
import java.net.URI;

/**
 * Created by daniel on 2017/2/15.
 * 注意 写是连续的并且不支持并发操作哦，写操作也是在byte全部被赋值的情况下才支持，目前writeBuffer仅支持到这样的程度
 * writeBuffer虽然提供了随机写的接口，但是实际上只支持连续写入
 * EditBuffer可以支持随机写入,并且写之后不再更改
 * 写入方法均不支持多线程
 */
public abstract class WriteBuffer extends AbstractBuffer implements Write {

    //1024
    public static final int DEFAULT_CAPACITY_OFFSET = 10;

    protected int current_max_size;

    protected int current_max_offset = DEFAULT_CAPACITY_OFFSET;

    protected int max_offset;

    protected int max_position = -1;

    protected volatile boolean flushed = false;

    protected volatile boolean changed = false;



    public boolean hasChanged() {
        return changed;
    }

    /**
     * 如果没写过或者写过了又更改了都需要重新写
     * @return
     */
    public boolean needFlush() {
        return !flushed || changed;
    }

    protected void checkIndex(int p) {
        if (ir(p)){
            return;
        }
        throw new BufferIndexOutOfBoundsException(p);
    }

    public boolean full() {
        return max_position  == max_size - 1;
    }

    /**
     *对于child edit来说 如果没改变是不用写文件的，就不会创建outputstream
     * @return
     */
    public final int getByteSize() {
        return getLength() << getLengthOffset();
    }

    public int getLength() {
        return (max_position + 1) ;
    }

    protected void loadContent() {
        //doNothing
    }

    protected final boolean ir(int p){
        return p > -1 && p < current_max_size;
    }

    protected WriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block);
        this.max_offset = max_offset;
        this.max_size = 1 << max_offset;
        this.directAccess = false;
    }

    protected WriteBuffer(Connector connector, URI uri) {
        super(connector, new FileBlock(uri));
        //不支持超过2G的文件
        this.max_offset = 31;
        this.max_size = Integer.MAX_VALUE;
        this.directAccess = true;
    }


    protected final void setCurrentCapacity(int offset) {
        this.current_max_offset = offset;
        this.current_max_size = 1 << offset;
    }


    protected void ensureCapacity(int position){
        if(position < max_size) {
            addCapacity(position);
            changed = true;
        } else {
            throw new BufferIndexOutOfBoundsException(position);
        }
    }

    private final void setMaxPosition(int position) {
        access();
        if(position > max_position ){
            max_position = position;
        }
    }

    protected final void addCapacity(int position) {
        while ( position >= current_max_size){
            addCapacity();
        }
        setMaxPosition(position);
    }

    protected void addCapacity() {
        int len = this.current_max_size << getLengthOffset();
        setCurrentCapacity(this.current_max_offset + 1);
        int newLen = this.current_max_size << getLengthOffset();
        beforeStatusChange();
        try {
            this.address = CacheManager.getInstance().allocateWrite((Buffer) this, address, len, newLen);
            allocateSize = newLen;
            MemoryUtils.fill0(this.address + len, newLen - len);

        } catch (OutOfMemoryError error){
            error.printStackTrace();
        }
        afterStatusChange();
    }

    public void force() {
        forceWrite();
        closeWithOutSync();
    }


    public void closeWithOutSync() {
        this.clear();
    }

    protected final void forceWrite() {
        int i = 0;
        while (needFlush()) {
            i++;
            SyncManager.getInstance().force(createWriteJob());
            //尝试3次依然抛错就不写了 强制释放内存 TODO后续考虑对异常未保存文件处理
            if(i > 3) {
                flushed = true;
                break;
            }
        }
    }


    protected JobAssist createWriteJob() {
        return new JobAssist(bufferKey, new Job() {
            public void doJob() {
                try {
                    write0();
                } catch (StreamCloseException e){
                    flushed = false;
                    //stream close这种还是直接触发写把，否则force的时候如果有三次那么就会出现写不成功的bug
                    //理论讲写方法都是单线程，所以force的时候肯定也不会再写了，但是不怕一万就怕万一
                    //这样执行下去job会唤醒force的while循环会执行一次会导致写次数++
                    //所以不trigger了直接循环执行把
                    doJob();
                }
            }
        });
    }

    private transient long lastWriteTime;
    //20秒内响应一次写
    private static volatile long PERIOD = 20000;

    public void write() {

        long t =  System.currentTimeMillis();
        if(t - lastWriteTime > PERIOD) {
            lastWriteTime = t;
            SyncManager.getInstance().triggerWork(createWriteJob());
        }

    }

    /**
     * 在clear的时候关闭
     */
    protected void close(){
        close = true;
        this.max_size = 0;
    }

    public void clear(){
        synchronized (this) {
            if(close){
                return;
            }
            close();
            this.current_max_size = 0;
            clearMemory();
            releaseBuffer();
        }
    }


    protected void clearAfterWrite() {
        clear();
    }

    protected void write0(){
        synchronized (this) {
            changed = false;
            try {
                bufferKey.getConnector().write(bufferKey.getBlock(), getInputStream());
                flushed = true;
                clearAfterWrite();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Level getLevel() {
        return Level.WRITE;
    }

}
