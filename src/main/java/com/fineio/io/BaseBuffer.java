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
import com.fineio.io.file.writer.JobFinishedManager;
import com.fineio.io.file.writer.SyncManager;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.manager.allocator.Allocator;
import com.fineio.memory.manager.allocator.impl.BaseMemoryAllocator;
import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.memory.manager.obj.impl.AllocateObject;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yee
 * @date 2018/9/19
 */
public abstract class BaseBuffer<R extends BufferR, W extends BufferW> implements Buffer {


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
    protected volatile int maxSize;
    protected volatile int maxOffset;
    protected volatile Listener listener;
    protected volatile MemoryObject memoryObject;

    private volatile boolean direct;


    private Lock lock = new ReentrantLock();

    public BaseBuffer(Connector connector, URI uri, Listener listener) {
        level = Level.INITIAL;
        syncStatus = SyncStatus.UNSUPPORTED;
        bufferKey = new BufferKey(connector, new FileBlock(uri));
        this.direct = true;
        this.listener = listener;
        this.uri = uri;
        this.maxOffset = 31;
        this.maxSize = Integer.MAX_VALUE;
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

    }

    @Override
    public int getLength() {
        return maxSize;
    }

    @Override
    public final boolean resentAccess() {
        return access || level == Level.WRITE;
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
        lock.lock();
        try {
            return address;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getAllocateSize() {
        lock.lock();
        try {
            return allocateSize;
        } finally {
            lock.unlock();
        }
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

    @Override
    public void close() {
        close = true;
    }

    @Override
    public abstract W asWrite();

    @Override
    public abstract R asRead();

    @Override
    public W asAppend() {
        syncStatus = SyncStatus.SYNC;
        try {
            return asRead().asWrite();
        } finally {
            syncStatus = SyncStatus.UNSUPPORTED;
        }
    }

    @Override
    public MemoryObject getFreeObject() {
        lock.lock();
        try {
            switch (level) {
                case WRITE:
                case INITIAL:
                    return null;
                case CLEAN:
                    MemoryObject object = new AllocateObject(address, allocateSize);
                    load = false;
                    allocateSize = 0;
                    maxSize = 0;
                    address = 0;
                    return object;
                case READ:
                    synchronized (this) {
                        if (syncStatus != SyncStatus.SYNC) {
                            MemoryObject obj = new AllocateObject(address, allocateSize);
                            load = false;
                            allocateSize = 0;
                            maxSize = 0;
                            address = 0;
                            return obj;
                        }
                    }
                default:
                    return null;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unLoad() {
        lock.lock();
        try {
            level = Level.INITIAL;
            load = false;
            allocateSize = 0;
            maxSize = 0;
            address = 0;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clearAfterClose() {

    }

    protected abstract class ReadBuffer implements Buffer {
        protected volatile long readAddress;

        public ReadBuffer() {
            level = Level.READ;
            if (address == 0) {
                checkRead0();
            } else {
                readAddress = address;
            }
        }

        @Override
        public <B extends Buffer> B asRead() {
            return (B) this;
        }

        @Override
        public <B extends Buffer> B asWrite() {
            return (B) BaseBuffer.this.asWrite();
        }

        @Override
        public <B extends Buffer> B asAppend() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SyncStatus getSyncStatus() {
            return BaseBuffer.this.getSyncStatus();
        }

        @Override
        public long getAddress() {
            return address;
        }

        @Override
        public void clearAfterClose() {
            listener.remove(BaseBuffer.this, BaseDeAllocator.Builder.READ);
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

        @Override
        public void close() {
        }

        @Override
        public boolean resentAccess() {
            return access;
        }

        @Override
        public void resetAccess() {
            access = false;
        }

        @Override
        public void unLoad() {
            BaseBuffer.this.unLoad();
        }

        @Override
        public MemoryObject getFreeObject() {
            return BaseBuffer.this.getFreeObject();
        }

        public final void checkRead0() {
            try {
                switch (level) {
                    case CLEAN:
                        level = Level.READ;
                        if (0 < maxSize && address != 0) {
                            load = true;
                            readAddress = address;
                        }
                        break;
                    case READ:
                        loadContent();
                        break;
                    case WRITE:
                        throw new RuntimeException("Writing");
                    default:
                }
            } catch (BufferConstructException ignore) {
            }
        }

        protected final void checkRead(int p) {
            if (!load || maxSize == 0 || address == 0) {
                load = false;
                loadContent();
                listener.update(this);
            }
            if (p < maxSize && p > -1) {
                if (!access) {
                    access = true;
                }
                return;
            }
            throw new BufferIndexOutOfBoundsException(uri, p, maxSize);
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
                        allocator = BaseMemoryAllocator.Builder.DIRECT.build(
                                bufferKey.getConnector().read(bufferKey.getBlock()));
                    }
                } catch (Exception e) {
                    throw new BufferConstructException(e);
                }
                memoryObject = MemoryManager.INSTANCE.allocate(allocator);
                readAddress = memoryObject.getAddress();
                lock.lock();
                try {
                    address = readAddress;
                    allocateSize = memoryObject.getAllocateSize();
                    load = true;
                    maxSize = (int) (allocateSize >> getOffset());
                } finally {
                    lock.unlock();
                }
            }
        }

        @Override
        public Level getLevel() {
            return Level.READ;
        }

        @Override
        public int getLength() {
            checkRead0();
            return maxSize;
        }
    }

    protected abstract class WriteBuffer implements BufferW {
        private static final int DEFAULT_CAPACITY_OFFSET = 10;
        protected int currentMaxSize;
        protected int currentMaxOffset = DEFAULT_CAPACITY_OFFSET;
        protected volatile int writeCurrentPosition = -1;
        protected volatile AtomicInteger status = new AtomicInteger(0);
        //20秒内响应一次写
        private volatile long PERIOD = 20000;
        /**
         * write start
         */
        private volatile boolean sync;
        private transient long lastWriteTime;
        private volatile boolean flushed;
        private volatile boolean changed;

        public WriteBuffer() {
            sync = false;
            if (direct) {
                maxOffset = 31;
                maxSize = Integer.MAX_VALUE;
            } else {
                switch (level) {
                    case READ:
                    case CLEAN:
                        if (maxSize > 0 && address > 0 && allocateSize > 0) {
                            int offset = Maths.log2(maxSize);
                            setCurrentCapacity(offset);
                            currentMaxSize = maxSize;
                            writeCurrentPosition = maxSize - 1;
                            MemoryManager.INSTANCE.flip(allocateSize, true);
                        }
                    default:
                }
                maxSize = 1 << maxOffset;
                JobFinishedManager.getInstance().addTask(uri);
            }
            level = Level.WRITE;

        }

        @Override
        public <B extends Buffer> B asWrite() {
            return (B) this;
        }

        @Override
        public <B extends Buffer> B asRead() {
            return (B) BaseBuffer.this.asRead();
        }

        @Override
        public <B extends Buffer> B asAppend() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean full() {
            return writeCurrentPosition >= maxSize - 1;
        }

        @Override
        public final void write() {
            long t = System.currentTimeMillis();
            if (t - lastWriteTime > PERIOD) {
                sync = true;
                lastWriteTime = t;
                syncStatus = SyncStatus.SYNC;
                if (level == Level.WRITE) {
                    SyncManager.getInstance().triggerWork(createWriteJob(direct));
                    if (!direct) {
                        flip();
                    }
                } else {
                    syncStatus = SyncStatus.UNSUPPORTED;
                }
            }
        }

        private final JobAssist createWriteJob(final boolean clear) {
            return new JobAssist(bufferKey, new Job() {
                private int tryTime = 0;

                @Override
                public void doJob() {
                    if (tryTime++ > 3) {
                        return;
                    }
                    try {
                        write0();
                        sync = false;
                        level = Level.CLEAN;
                        syncStatus = SyncStatus.UNSUPPORTED;
                        if (clear) {
                            if (direct) {
                                listener.remove(BaseBuffer.this, BaseDeAllocator.Builder.WRITE);
                            } else {
                                listener.remove(BaseBuffer.this, BaseDeAllocator.Builder.READ);
                            }
                        }
                    } catch (StreamCloseException e) {
                        flushed = false;
                        //stream close这种还是直接触发写把，否则force的时候如果有三次那么就会出现写不成功的bug
                        //理论讲写方法都是单线程，所以force的时候肯定也不会再写了，但是不怕一万就怕万一
                        //这样执行下去job会唤醒force的while循环会执行一次会导致写次数++
                        //所以不trigger了直接循环执行把
                        FineIOLoggers.getLogger().error(e);
                        doJob();
                    }
                }
            });
        }

        protected final boolean needFlush() {
            return !flushed || changed;
        }

        @Override
        public final void force() {
            syncStatus = SyncStatus.SYNC;
            if (level == Level.WRITE) {
                sync = true;
                if (!direct) {
                    flip();
                }
                forceWrite(direct);
            } else {
                syncStatus = SyncStatus.UNSUPPORTED;
            }
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

        public final void flip() {
            level = Level.READ;
            MemoryManager.INSTANCE.flip(allocateSize, false);
            if (sync) {
                syncStatus = SyncStatus.SYNC;
            }
            if (writeCurrentPosition >= 0) {
                maxSize = writeCurrentPosition + 1;
                load = true;
                memoryObject = new AllocateObject(address, allocateSize);
            }
        }

        private final InputStream getInputStream() {
            if (getAddress() == 0) {
                throw new StreamCloseException();
            }
            DirectInputStream inputStream = new DirectInputStream(getAddress(), (writeCurrentPosition + 1) << getOffset(), new StreamCloseChecker(status.get()) {
                @Override
                public boolean check() {
                    return WriteBuffer.this.status.get() == getStatus();
                }
            });

            return inputStream;
        }

        protected void ensureCapacity(int position) {
            if (position < maxSize && !close) {
                addCapacity(position);
                changed = true;
            } else {
                throw new BufferIndexOutOfBoundsException(uri, position, maxSize);
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
                    allocator = BaseMemoryAllocator.Builder.DIRECT.build(address, len, newLen);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            status.incrementAndGet();
            MemoryObject object = MemoryManager.INSTANCE.allocate(allocator);
            address = object.getAddress();
            allocateSize = newLen;
            status.incrementAndGet();
        }

        protected final void setCurrentCapacity(int offset) {
            this.currentMaxOffset = offset;
            this.currentMaxSize = 1 << offset;
        }

        @Override
        public Level getLevel() {
            return Level.WRITE;
        }

        @Override
        public SyncStatus getSyncStatus() {
            return BaseBuffer.this.getSyncStatus();
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

        @Override
        public void close() {
            force();
        }

        @Override
        public void clearAfterClose() {
            syncStatus = SyncStatus.SYNC;
            if (level == Level.WRITE) {
                sync = true;
                if (!direct) {
                    flip();
                }
                forceWrite(true);
            } else {
                syncStatus = SyncStatus.UNSUPPORTED;
                listener.remove(BaseBuffer.this, BaseDeAllocator.Builder.READ);
            }
        }

        @Override
        public boolean resentAccess() {
            return access;
        }

        @Override
        public void resetAccess() {
            access = false;
        }

        @Override
        public void unLoad() {
            BaseBuffer.this.unLoad();
        }

        @Override
        public MemoryObject getFreeObject() {
            return BaseBuffer.this.getFreeObject();
        }

        @Override
        public int getLength() {
            return writeCurrentPosition + 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseBuffer that = (BaseBuffer) o;

        return bufferKey != null ? bufferKey.equals(that.bufferKey) : that.bufferKey == null;
    }

    @Override
    public int hashCode() {
        return bufferKey != null ? bufferKey.hashCode() : 0;
    }
}