package com.fineio.io.read;

import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.FileCloseException;
import com.fineio.io.Buffer;
import com.fineio.io.base.AbstractBuffer;
import com.fineio.io.base.BufferKey;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadIOFile;
import com.fineio.io.file.writer.ForceManager;
import com.fineio.io.file.writer.SyncTask;
import com.fineio.io.mem.MemBean;
import com.fineio.io.mem.MemBeanContainer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by daniel on 2017/2/9.
 */
public abstract class ReadBuffer extends AbstractBuffer implements Read, Observer {
    private volatile AtomicBoolean load = new AtomicBoolean(false);
    protected int max_byte_len;
    protected volatile AtomicBoolean useProxy = new AtomicBoolean(false);

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


    public LEVEL getLevel() {
        return LEVEL.READ;
    }

    /**
     * max_offset 为什么要作为参数传进来而不是从connector里面读呢 是因为可能我上次写的cube的时候配置的4M 后来改成了64M这样的情况下读取connecter的值就会导致原来的值不对
     * 因为File里面获取到的offset是去掉类型偏移量的值，所以这里的offset需要加上偏移量，纯粹是比较2，不过这里都是不对外公开的，无所谓拉
     * 所以这个max_offset是传进来的，并且是当前文件的offset的值
     *
     * @param connector
     * @param block
     * @param max_offset
     * @see ReadIOFile
     */
    protected ReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block);
        this.max_byte_len = 1 << (max_offset + getLengthOffset());
        this.directAccess = false;
        setMemBean();
    }

    protected ReadBuffer(Connector connector, URI uri) {
        super(connector, new FileBlock(uri));
        this.directAccess = true;
        setMemBean();
    }

    private void setMemBean() {
        MemBean bean = MemBeanContainer.getContainer().get(getUri());
        if (null != bean && !bean.isClose()) {
            useProxy.set(true);
            bean.addObserver(this);
            load.set(bean.isLoad());
            max_size = bean.getMaxSize();
            address = bean.getAddress();
        }
    }

    protected void loadContent() {
        loadData();
    }


    private final void loadData() {
        synchronized (this) {
            if (load.get()) {
                return;
            }
            if (close) {
                throw new FileCloseException();
            }
            if (directAccess) {
                DirectAccess();
            } else {
                VirtualAccess();
            }
        }
    }

    private void DirectAccess() {
        InputStream is = null;
        try {
            is = bufferKey.getConnector().read(bufferKey.getBlock());
            if (is == null) {
                throw new BlockNotFoundException("block:" + bufferKey.getBlock().toString() + " not found!");
            }
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int len = 0;
            while ((len = is.read(bytes, 0, bytes.length)) > 0) {
                byteArrayOutputStream.write(bytes, 0, len);
            }
            bytes = byteArrayOutputStream.toByteArray();
            int off = bytes.length;
            allocateMemory(bytes, off);
        } catch (IOException e) {
            throw new BlockNotFoundException("block:" + bufferKey.getBlock().toString() + " not found!");
        } catch (OutOfMemoryError error) {
            //todo 预防内存设置超大 赋值的时候发生溢出
            error.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void allocateMemory(byte[] bytes, int off) {
        beforeStatusChange();
        address = CacheManager.getInstance().allocateRead((Buffer) this, off);
        MemoryUtils.copyMemory(bytes, address, off);
        allocateSize = off;
        load.set(true);
        max_size = off >> getLengthOffset();
        afterStatusChange();
    }

    private void VirtualAccess() {
        InputStream is = null;
        try {
            is = bufferKey.getConnector().read(bufferKey.getBlock());
            if (is == null) {
                throw new BlockNotFoundException("block:" + bufferKey.getBlock().toString() + " not found!");
            }
            byte[] bytes = new byte[max_byte_len];
            int off = 0;
            int len = 0;
            while ((len = is.read(bytes, off, max_byte_len - off)) > 0) {
                off += len;
            }
            allocateMemory(bytes, off);
        } catch (IOException e) {
            throw new BlockNotFoundException("block:" + bufferKey.getBlock().toString() + " not found!");
        } catch (OutOfMemoryError error) {
            //todo 预防内存设置超大 赋值的时候发生溢出
            error.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public boolean full() {
        return (max_byte_len >> getLengthOffset()) == max_size;
    }

    protected final void checkIndex(int p) {
        if (ir(p)) {
            access();
//            if (useProxy) {
//                ForceManager.getInstance().accessTask(SyncTask.taskKey(bufferKey));
//            }
            return;
        }
        lc(p);
    }

    private final boolean ir(int p) {
        if (useProxy.get()) {
            MemBean bean = MemBeanContainer.getContainer().get(getUri());
            if (null == bean || bean.isClose() || bean.getMaxSize() == 0) {
                synchronized (this) {
                    useProxy.set(false);
                    load.set(false);
                    return false;
                }
            }
        }
        return p > -1 && p < max_size;
    }

    private final void lc(int p) {
        synchronized (this) {
            if (load.get()) {
                if (ir(p)) {
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
            if (useProxy.get()) {
                if (!ForceManager.getInstance().isFlushed(bufferKey)) {
//                    doClear();
                    ForceManager.getInstance().doSync(SyncTask.taskKey(bufferKey));
                }
            } else {
                doClear();
            }
        }
    }

    private void doClear() {
        if (!load.get()) {
            return;
        }
        load.set(false);
        max_size = 0;
        clearMemory();
        releaseBuffer();
    }

    public void force() {
        closeWithOutSync();
    }

    public void closeWithOutSync() {
        synchronized (this) {
            if (close) {
                return;
            }
            close = true;
            clear();
        }
    }

    public BufferKey getBufferKey() {
        return bufferKey;
    }

    public void setMaxSize(int maxSize) {
        max_size = maxSize;
    }

    public void setAddress(long address) {
        this.address = address;
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MemBean) {
            synchronized (this) {
                MemBean bean = (MemBean) o;
                if (useProxy.get() && getUri().equals(bean.getUri())) {
                    max_size = bean.getMaxSize();
                    load.set(bean.isLoad());
                    address = bean.getAddress();
                    useProxy.set(false);
                }
            }
        }
    }
}
