package com.fineio.io;


import com.fineio.base.Maths;
import com.fineio.cache.SyncStatus;
import com.fineio.exception.BufferConstructException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.StreamCloseException;
import com.fineio.io.base.BufferKey;
import com.fineio.io.base.DirectInputStream;
import com.fineio.io.base.Job;
import com.fineio.io.base.JobAssist;
import com.fineio.io.base.StreamCloseChecker;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.writer.SyncManager;
import com.fineio.memory.manager.allocator.Allocator;
import com.fineio.memory.manager.allocator.impl.BaseMemoryAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.memory.manager.obj.impl.AllocateObject;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yee
 * @date 2018/9/19
 */
public abstract class BaseBuffer implements Buffer {

    private static final int DEFAULT_CAPACITY_OFFSET = 10;
    //20秒内响应一次写
    private static volatile long PERIOD = 20000;
    /**
     * common
     */
    protected final BufferKey bufferKey;
    protected volatile Level level;
    protected volatile SyncStatus syncStatus;
    protected volatile long address;
    protected volatile long allocateSize;
    protected volatile URI uri;
    protected volatile boolean close;
    protected volatile boolean access;
    /**
     * read start
     */
    protected volatile int maxLength;
    protected volatile boolean load;
    protected volatile int readMaxPosition;
    protected volatile int maxOffset;
    protected volatile Listener listener;
    protected volatile MemoryObject memoryObject;
    protected int currentMaxSize;
    protected int currentMaxOffset = DEFAULT_CAPACITY_OFFSET;
    protected volatile int writeMaxPosition = -1;
    protected volatile int writeCurrentPosition = -1;
    protected volatile AtomicInteger status = new AtomicInteger(0);
    private volatile boolean direct;
    private volatile boolean changed;
    /**
     * write start
     */
    private volatile boolean sync;
    private transient long lastWriteTime;
    private volatile boolean flushed;

    public BaseBuffer(Connector connector, URI uri, Listener listener) {
        level = Level.INITIAL;
        syncStatus = SyncStatus.UNSUPPORTED;
        bufferKey = new BufferKey(connector, new FileBlock(uri));
        this.direct = true;
        this.listener = listener;
        this.uri = uri;
    }

    public BaseBuffer(Connector connector, FileBlock block, int maxOffset, Listener listener) {
        level = Level.INITIAL;
        syncStatus = SyncStatus.UNSUPPORTED;
        bufferKey = new BufferKey(connector, block);
        this.direct = false;
        this.maxOffset = maxOffset;
        this.maxLength = 1 << (this.maxOffset + getOffset());
        this.listener = listener;
        this.uri = block.getBlockURI();
        this.writeMaxPosition = 1 << maxOffset;
    }

    @Override
    public final boolean resentAccess() {
        return access;
    }

    @Override
    public final void resetAccess() {
        access = false;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    @Override
    public long getAddress() {
        return address;
    }

    @Override
    public long getAllocateSize() {
        return allocateSize;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public boolean isDirect() {
        return direct;
    }

    @Override
    public boolean isClose() {
        return close;
    }

    protected abstract int getOffset();

    public final void checkRead0() {
        try {
            if (-1 < writeCurrentPosition && address != 0) {
                readMaxPosition = writeMaxPosition + 1;
                load = true;
            }
            loadContent();
        } catch (Exception ignore) {
        }
    }

    protected final void checkRead(int p) {
        if (!load) {
            loadContent();
            listener.update(this);
        }
        if (p < readMaxPosition && p > -1) {
            if (!access) {
                access = true;
            }
            return;
        }
        throw new BufferIndexOutOfBoundsException(uri, p, readMaxPosition);
    }

    protected void ensureCapacity(int position) {
        if (position < writeMaxPosition && !close) {
            addCapacity(position);
            changed = true;
        } else {
            throw new BufferIndexOutOfBoundsException(uri, position, writeMaxPosition);
        }
    }

    protected final void addCapacity(int position) {
        while (position >= currentMaxSize) {
            addCapacity();
        }
        setMaxPosition(position);
    }

    private final void setMaxPosition(int position) {
        if (!access) {
            access = true;
        }
        if (position > writeCurrentPosition) {
            writeCurrentPosition = position;
        }
    }

    protected void addCapacity() {
        int len = this.currentMaxSize << getOffset();
        setCurrentCapacity(this.currentMaxOffset + 1);
        int newLen = this.currentMaxSize << getOffset();
        Allocator allocator;
        try {
            if (!direct) {
                allocator = BaseMemoryAllocator.Builder.BLOCK.build(address, len, newLen);
            } else {
                allocator = BaseMemoryAllocator.Builder.SMALL.build(address, len, newLen);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        status.incrementAndGet();
        MemoryObject object = MemoryManager.INSTANCE.allocate(allocator);
        this.address = object.getAddress();
        this.allocateSize = newLen;
        status.incrementAndGet();
    }

    protected final void setCurrentCapacity(int offset) {
        this.currentMaxOffset = offset;
        this.currentMaxSize = 1 << offset;
    }

    protected void loadContent() {
        synchronized (this) {
            level = Level.READ;
            if (load) {
                return;
            }
            if (close) {
                close = false;
            }
            Allocator allocator;
            try {
                if (!direct) {
                    allocator = BaseMemoryAllocator.Builder.BLOCK.build(
                            bufferKey.getConnector().read(bufferKey.getBlock()), maxLength);
                } else {
                    allocator = BaseMemoryAllocator.Builder.SMALL.build(
                            bufferKey.getConnector().read(bufferKey.getBlock()));
                }
            } catch (Exception e) {
                throw new BufferConstructException(e);
            }
            memoryObject = MemoryManager.INSTANCE.allocate(allocator);
            this.address = memoryObject.getAddress();
            this.allocateSize = memoryObject.getAllocateSize();
            this.load = true;
            this.readMaxPosition = (int) (this.allocateSize >> getOffset());
        }
    }

    @Override
    synchronized public void flip() {
        switch (level) {
            case WRITE:
                MemoryManager.INSTANCE.flip(allocateSize, false);
                if (sync) {
                    syncStatus = SyncStatus.SYNC;
                }
                if (writeCurrentPosition > 0) {
                    readMaxPosition = writeCurrentPosition + 1;
                    load = true;
                    memoryObject = new AllocateObject(address, allocateSize);
                }
                level = Level.READ;
                break;
            case CLEAN:
            case READ:
                if (null != memoryObject) {
                    address = memoryObject.getAddress();
                    allocateSize = memoryObject.getAllocateSize();
                }
                memoryObject = null;
                if (direct) {
                    writeMaxPosition = Integer.MAX_VALUE;
                    maxOffset = 31;
                } else {
                    if (readMaxPosition > 0) {
                        int offset = Maths.log2(readMaxPosition);
                        writeCurrentPosition = readMaxPosition - 1;
                        setCurrentCapacity(offset);
                        currentMaxSize = readMaxPosition;
                    }
                    writeMaxPosition = 1 << maxOffset;
                }
                level = Level.WRITE;
                sync = false;
                MemoryManager.INSTANCE.flip(allocateSize, true);
                break;
            default:
        }
    }

    public final boolean full() {
        if (level != Level.WRITE) {
            return false;
        }
        return writeCurrentPosition >= writeMaxPosition - 1;
    }

    public final void write() {
        long t = System.currentTimeMillis();
        if (t - lastWriteTime > PERIOD) {
            sync = true;
            lastWriteTime = t;
            syncStatus = SyncStatus.SYNC;
            SyncManager.getInstance().triggerWork(createWriteJob(direct));
            if (!direct) {
                flip();
            }
        }
    }

    private final JobAssist createWriteJob(final boolean clear) {
        return new JobAssist(bufferKey, new Job() {
            @Override
            public void doJob() {
                try {
                    write0();
                    sync = false;
                    if (clear) {
                        listener.remove(BaseBuffer.this);
                    }
                    level = Level.CLEAN;
                    syncStatus = SyncStatus.UNSUPPORTED;
                } catch (StreamCloseException e) {
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

    protected final boolean needFlush() {
        return !flushed || changed;
    }

    public void force() {
        syncStatus = SyncStatus.SYNC;
        if (!direct) {
            flip();
        }
        forceWrite(direct);
    }

    private final void forceWrite(boolean clear) {
        int i = 0;
        while (needFlush()) {
            i++;
            SyncManager.getInstance().force(createWriteJob(clear));
            //尝试3次依然抛错就不写了 强制释放内存 TODO后续考虑对异常未保存文件处理
            if (i > 3) {
                flushed = true;
                break;
            }
        }
    }

    private final void write0() {
        synchronized (this) {
            changed = false;
            try {
                bufferKey.getConnector().write(bufferKey.getBlock(), getInputStream());
                flushed = true;
            } catch (IOException e) {
            }
        }
    }

    private final InputStream getInputStream() {
        if (getAddress() == 0) {
            throw new StreamCloseException();
        }
        DirectInputStream inputStream = new DirectInputStream(getAddress(), (writeCurrentPosition + 1) << getOffset(), new StreamCloseChecker(status.get()) {
            @Override
            public boolean check() {
                return BaseBuffer.this.status.get() == getStatus();
            }
        });

        return inputStream;
    }

    @Override
    public void close() {
        if (level == Level.WRITE) {
            force();
        }
        close = true;
    }

    public synchronized void asWrite() {
        switch (level) {
            case READ:
            case CLEAN:
                flip();
                break;
            case WRITE:
                break;
            default:
                if (!direct) {
                    writeMaxPosition = 1 << maxOffset;
                } else {
                    writeMaxPosition = Integer.MAX_VALUE;
                    maxOffset = 31;
                }
        }

        level = Level.WRITE;
        if (!direct) {
            writeMaxPosition = 1 << maxOffset;
        } else {
            writeMaxPosition = Integer.MAX_VALUE;
            maxOffset = 31;
        }
    }

    @Override
    public void unLoad() {
        level = Level.INITIAL;
        load = false;
        allocateSize = 0;
        readMaxPosition = 0;
        address = 0;
    }
}
