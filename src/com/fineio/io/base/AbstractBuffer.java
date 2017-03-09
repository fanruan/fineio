package com.fineio.io.base;

import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.StreamCloseException;
import com.fineio.io.Buffer;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by daniel on 2017/2/15.
 */
public abstract class AbstractBuffer implements BaseBuffer {

    /**
     * 不再重新赋值
     */


    protected final BufferKey bufferKey;


    protected volatile long address;
    protected volatile int max_size;
    private volatile AtomicInteger status = new AtomicInteger(0);
    protected volatile boolean close = false;
    private  volatile boolean access = false;
    protected volatile int allocateSize = 0;



    public  int getAllocateSize() {
        return  allocateSize;
    }


    /**
     * 当释放之后会改变status check的状态变化不会再做get操作
     * 创建get方法
     * @return
     */
    protected final InputStream getInputStream() {
        if(address == 0) {
            throw new StreamCloseException();
        }
        DirectInputStream inputStream =  new DirectInputStream(address, getByteSize(), new StreamCloseChecker(status.get()) {
            public boolean check() {
                return status.get() == getStatus() ;
            }
        });

        return inputStream;
    }

    protected final void access(){
        if(!access){
            access = true;
        }
    }

    /**
     * 是否刚被访问过
     * @return
     */
    public boolean recentAccess(){
        return access;
    }

    /**
     * 重置access状态
     */
    public void resetAccess() {
        access = false;
    }

    /**
     * 这个方法前后都要做一次，否则会出现创建inputstream的时候已经变化 或者未响应变化
     */
    protected void afterStatusChange() {
        status.addAndGet(1);
    }

    /**
     * 这个方法前后都要做一次，否则会出现创建inputstream的时候已经变化 或者未响应变化
     */
    protected void beforeStatusChange() {
        status.addAndGet(1);
        //等待1微秒
        LockSupport.parkNanos(1000);
    }

    /**
     * 这个大小在clear前后是不一样的，释放内存的时候需要在clear之前获取一下大小
     * @return
     */
    protected int getByteSize() {
        return max_size << getLengthOffset();
    }


    protected AbstractBuffer(Connector connector, FileBlock block) {
        this.bufferKey = new BufferKey(connector, block);
        CacheManager.getInstance().registerBuffer((Buffer) this);
    }

    public void clear() {
        synchronized (this) {
            beforeStatusChange();
            MemoryUtils.free(address);
            afterStatusChange();
            //close true才需要remove掉
            CacheManager.getInstance().releaseBuffer((Buffer) this, close);
        }
    }

    /**
     *基础类型相对于byte类型的偏移量比如int是4个byte那么便宜量是2
     * long是8个byte那么偏移量是3
     * 不放在接口里面是因为protected类型
     * @return
     */
    protected abstract int getLengthOffset();

}
