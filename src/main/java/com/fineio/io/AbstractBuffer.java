package com.fineio.io;

import com.fineio.base.Maths;
import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.cache.Watcher;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.FileCloseException;
import com.fineio.exception.StreamCloseException;
import com.fineio.io.base.BufferKey;
import com.fineio.io.base.Job;
import com.fineio.io.base.JobAssist;
import com.fineio.io.edit.EditBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.writer.SyncManager;
import com.fineio.io.read.ReadOnlyBuffer;
import com.fineio.io.write.WriteOnlyBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author yee
 * @date 2018/5/30
 */
public abstract class AbstractBuffer<R extends ReadOnlyBuffer, W extends WriteOnlyBuffer, E extends EditBuffer> implements Buffer {
    private static final int DEFAULT_CAPACITY_OFFSET = 10;
    private final static int MAX_COUNT = 1024;
    //20秒内响应一次写
    private static volatile long PERIOD = 20000;
    protected final BufferKey bufferKey;
    protected volatile long address;
    protected volatile int maxSize;
    protected volatile boolean close = false;
    protected volatile int allocateSize = 0;
    protected volatile boolean directAccess;
    protected volatile int maxOffset;
    protected volatile boolean changed;
    protected CacheManager manager;
    protected volatile boolean load;
    private volatile AtomicInteger status = new AtomicInteger(0);
    private volatile boolean access = false;
    private volatile LEVEL level;
    private volatile R readBuffer;
    private volatile W writeBuffer;
    private volatile E editBuffer;
    private volatile boolean flushed;
    private URI uri;
    private volatile int waitHelper = 0;
    private AtomicClearLong reference = new AtomicClearLong(createWatcher());
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected AbstractBuffer(Connector connector, FileBlock block, int maxOffset) {
        this.bufferKey = new BufferKey(connector, block);
        this.directAccess = false;
        this.maxOffset = maxOffset;
        this.uri = block.getBlockURI();
        this.level = LEVEL.READ;
        this.manager = CacheManager.getInstance();
    }

    protected AbstractBuffer(Connector connector, URI uri) {
        this.bufferKey = new BufferKey(connector, new FileBlock(uri));
        this.directAccess = true;
        this.uri = uri;
        this.level = LEVEL.READ;
        this.manager = CacheManager.getInstance();
    }

    private Watcher createWatcher() {
        return new Watcher() {
            @Override
            public void watch(long change) {
                if (change <= 0) {
                    synchronized (AbstractBuffer.this) {
                        if (!close) {
                            close = true;
                            maxSize = 0;
                            changed = false;
                            clear();
                            address = 0;
                            allocateSize = 0;
                        }
                    }
                }
            }
        };
    }

    protected abstract void exitPool();

    @Override
    public boolean recentAccess() {
        return level != LEVEL.READ || access;
    }

    @Override
    public void resetAccess() {
        access = false;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    public void unReference() {
        reference.decrement();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractBuffer that = (AbstractBuffer) o;

        return uri != null ? uri.equals(that.uri) : that.uri == null;
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }

    @Override
    public long getAddress() {
        return address;
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public int getAllocateSize() {
        return allocateSize;
    }

    @Override
    public boolean isDirect() {
        return directAccess;
    }

    @Override
    public int getStatus() {
        return status.get();
    }

    @Override
    public void close() {
        reference.set(0, false);
        clear();
    }

    @Override
    public void closeWithOutSync() {
        reference.set(0, true);
    }

    protected void clear() {
        switch (level) {
            case READ:
                readBuffer.closeWithOutSync();
                break;
            case WRITE:
                writeBuffer.forceAndClear();
                break;
            case EDIT:
                editBuffer.forceAndClear();
                break;
        }
    }

    @Override
    public LEVEL getLevel() {
        return level;
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
        //等待1024计算更加危险的状态控制
        int count = waitHelper + MAX_COUNT;
        while (waitHelper++ < count) {
        }
        waitHelper = 0;
    }

    public final R readOnlyBuffer() {
        if (level == LEVEL.READ) {
            if (null == readBuffer) {
                synchronized (this) {
                    if (null == readBuffer) {
                        if (0 != address) {
                            load = true;
                        }
                        readBuffer = createReadOnlyBuffer();
                    }
                }
            }
            reference.increment();
            return readBuffer;
        }
        throw new RuntimeException("Buffer cannot convert to readBuffer because it is being modified");
    }

    protected abstract R createReadOnlyBuffer();

    public final W writeOnlyBuffer() {
        lock.writeLock().lock();
        try {
            if (level != LEVEL.READ) {
                throw new RuntimeException("Buffer cannot convert to writeBuffer because it is already being modified");
            }

            level = LEVEL.WRITE;
            if (null == writeBuffer) {
                synchronized (this) {
                    if (null == writeBuffer) {
                        writeBuffer = createWriteOnlyBuffer();
                    }
                }
            }
            return writeBuffer;
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected abstract W createWriteOnlyBuffer();

    public final E editBuffer() {
        lock.writeLock().lock();
        try {
            if (level != LEVEL.READ) {
                throw new RuntimeException("Buffer cannot convert to editBuffer because it is already being modified");
            }
            level = LEVEL.EDIT;
            if (null == editBuffer) {
                synchronized (this) {
                    if (null == editBuffer) {
                        editBuffer = createEditBuffer();
                    }
                }
            }
            return editBuffer;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int getByteSize() {
        return getLength() << getOffset();
    }

    @Override
    public int getLength() {
        switch (level) {
            case READ:
                return readBuffer.getLength();
            case WRITE:
                return writeBuffer.getLength();
            case EDIT:
                return editBuffer.getLength();
            default:
                return maxSize;
        }
    }

    protected abstract E createEditBuffer();

    protected abstract class InnerReadBuffer extends BaseBuffer implements ReadOnlyBuffer {
        protected int maxByteLen;
        protected int allocateSize;

        public InnerReadBuffer() {
            super(AbstractBuffer.this);
            if (!buffer.isDirect()) {
                maxByteLen = 1 << (maxOffset + getOffset());
            }
            allocateSize = buffer.allocateSize;
        }

        @Override
        public int getAllocateSize() {
            return allocateSize;
        }

        @Override
        public LEVEL getLevel() {
            return LEVEL.READ;
        }

        @Override
        public boolean isLoad() {
            return buffer.load;
        }

        @Override
        public void access() {
            if (!access) {
                access = true;
            }
        }

        @Override
        public void close() {
            if (buffer.isDirect()) {
                buffer.closeWithOutSync();
            } else {
                reference.decrement();
            }
        }

        @Override
        protected void loadContent() {
            synchronized (buffer) {
                if (buffer.load) {
                    return;
                }
                if (buffer.close) {
                    throw new FileCloseException();
                }
                Accessor accessor = null;
                if (buffer.directAccess) {
                    accessor = new DirectAccessor(bufferKey);
                } else {
                    accessor = new VirtualAccessor(bufferKey, maxByteLen);
                }
                try {
                    accessor.invoke();
                    allocateMemory(accessor.getBytes(), accessor.getOff());
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void clear() {
            if (buffer.isDirect()) {
                buffer.closeWithOutSync();
            }
        }

        private void setAllocateSize(int allocateSize) {
            this.allocateSize = allocateSize;
            buffer.allocateSize = allocateSize;
        }

        @Override
        public void closeWithOutSync() {
            if (load && address != 0) {
                load = false;
                beforeStatusChange();
                MemoryUtils.free(address);
                afterStatusChange();
                manager.returnMemory(this, getLevel());
                exitPool();
                manager = null;
            }
        }

        @Override
        protected void check(int position) {
            if (level != LEVEL.READ) {
                throw new RuntimeException("Writing");
            }
            if (ir(position)) {
                access();
                return;
            }
            lc(position);
        }

        private final boolean ir(int p) {
            return p > -1 && p < buffer.maxSize;
        }

        private final void lc(int p) {
            synchronized (buffer) {
                if (buffer.load) {
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
            loadContent();
            check(p);
        }

        private void allocateMemory(byte[] bytes, int off) {
            beforeStatusChange();
            buffer.address = manager.allocateRead(off);
            MemoryUtils.copyMemory(bytes, buffer.address, off);
            setAllocateSize(off);
            buffer.load = true;
            buffer.maxSize = off >> getOffset();
            afterStatusChange();
        }
    }


    protected abstract class InnerWriteBuffer extends BaseBuffer implements WriteOnlyBuffer {

        protected int current_max_size;

        protected int current_max_offset = DEFAULT_CAPACITY_OFFSET;
        protected int maxPosition = -1;
        private transient long lastWriteTime;

        private boolean needClear;

        public InnerWriteBuffer() {
            super(AbstractBuffer.this);
            if (directAccess) {
                maxSize = Integer.MAX_VALUE;
                maxOffset = 31;
                needClear = true;
            } else {
                maxSize = 1 << maxOffset;
            }
        }

        @Override
        public boolean needClear() {
            return needClear;
        }

        protected void flip() {
            level = LEVEL.READ;
        }

        protected void setCurrentCapacity(int offset) {
            this.current_max_offset = offset;
            this.current_max_size = 1 << offset;
        }

        @Override
        public boolean hasChanged() {
            return changed;
        }

        @Override
        protected void loadContent() {
        }

        @Override
        public int getLength() {
            return maxPosition + 1;
        }

        @Override
        protected void check(int p) {
            if (ir(p)) {
                return;
            }
            throw new BufferIndexOutOfBoundsException(p);
        }

        protected void ensureCapacity(int position) {
            if (position < maxSize && !close) {
                addCapacity(position);
                changed = true;
            } else {
                throw new BufferIndexOutOfBoundsException(position);
            }
        }

        private final void setMaxPosition(int position) {
            if (!access) {
                access = true;
            }
            if (position > maxPosition) {
                maxPosition = position;
            }
        }

        protected final void addCapacity(int position) {
            while (position >= current_max_size) {
                addCapacity();
            }
            setMaxPosition(position);
        }

        protected void addCapacity() {
            int len = this.current_max_size << getOffset();
            setCurrentCapacity(this.current_max_offset + 1);
            int newLen = this.current_max_size << getOffset();
            beforeStatusChange();
            try {
                address = manager.allocateWrite(address, len, newLen);
                allocateSize = newLen;
                MemoryUtils.fill0(address + len, newLen - len);

            } catch (OutOfMemoryError error) {
                error.printStackTrace();
            }
            afterStatusChange();
        }

        protected final boolean ir(int p) {
            return p > -1 && p < current_max_size;
        }

        @Override
        public boolean full() {
            return maxPosition == maxSize - 1;
        }

        @Override
        public void write() {

            long t = System.currentTimeMillis();
            if (t - lastWriteTime > PERIOD) {
                lastWriteTime = t;
                level = LEVEL.READ;
                SyncManager.getInstance().triggerWork(createWriteJob(buffer.isDirect()));
                if (!buffer.isDirect()) {
                    readBuffer = readOnlyBuffer();
                    reference.decrementWithoutWatch();
                }
            }

        }

        protected void returnMemoryIfNeed() {
            if (allocateSize != 0) {
                manager.returnMemory(this, getLevel());
//                if (buffer.isDirect()) {
//                    manager = null;
//                }
                allocateSize = 0;
            }
        }

        @Override
        public void closeWithOutSync() {
            if (address != 0) {
                close = true;
                load = false;
                beforeStatusChange();
                MemoryUtils.free(address);
                afterStatusChange();
                returnMemoryIfNeed();
                exitPool();
                address = 0;
            }
        }

        @Override
        public LEVEL getLevel() {
            return LEVEL.WRITE;
        }

        @Override
        public void force() {
            forceWrite(buffer.isDirect());
            level = LEVEL.READ;
            if (!buffer.isDirect()) {
                readBuffer = readOnlyBuffer();
                reference.decrementWithoutWatch();
            }
            returnMemoryIfNeed();
            if (buffer.isDirect()) {
                manager = null;
            }
        }

        @Override
        public void forceAndClear() {
            needClear = true;
            forceWrite(true);
            returnMemoryIfNeed();
            exitPool();
            manager = null;
        }

        protected final void forceWrite(boolean clear) {
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

        protected boolean needFlush() {
            return !flushed || changed;
        }

        protected JobAssist createWriteJob(final boolean clear) {
            return new JobAssist(bufferKey, new Job() {
                @Override
                public void doJob() {
                    try {
                        write0();
                        if (clear) {
                            closeWithOutSync();
                        }
//                        level = LEVEL.READ;
//                        if (!clear) {
//                            readBuffer = readOnlyBuffer();
//                            reference.decrementWithoutWatch();
//                        }
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

        protected void write0() {
            synchronized (this) {
                changed = false;
                try {
                    bufferKey.getConnector().write(bufferKey.getBlock(), getInputStream());
                    flushed = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void close() {

        }
    }

    protected abstract class InnerEditBuffer extends InnerWriteBuffer implements EditBuffer {

        public InnerEditBuffer() {
            if (address != 0) {
                load = true;
                maxPosition = super.getLength() - 1;
            }
        }

        @Override
        public LEVEL getLevel() {
            return LEVEL.EDIT;
        }

        @Override
        protected void ensureCapacity(int position) {
            if (!load) {
                loadContent();
            }
            if (position < maxSize && !close) {
                addCapacity(position);
            } else {
                throw new BufferIndexOutOfBoundsException(position);
            }
        }

        @Override
        protected void check(int p) {
            if (ir(p)) {
                access();
                return;
            }
            lc(p);
        }

        private final void lc(int p) {
            synchronized (this) {
                if (load) {
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
            loadContent();
            check(p);
        }

        @Override
        protected void addCapacity() {
            int len = this.current_max_size << getOffset();
            setCurrentCapacity(this.current_max_offset + 1);
            int newLen = this.current_max_size << getOffset();
            beforeStatusChange();
            try {
                address = manager.allocateEdit(address, len, newLen);
                allocateSize = newLen;
                MemoryUtils.fill0(address + len, newLen - len);

            } catch (OutOfMemoryError error) {
                error.printStackTrace();
            }
            afterStatusChange();
        }

//        @Override
//        public void force() {
//            forceWrite(buffer.isDirect());
//        }


        @Override
        public void closeWithOutSync() {
            if (address != 0) {
                close = true;
                load = false;
                beforeStatusChange();
                MemoryUtils.free(address);
                afterStatusChange();
                super.returnMemoryIfNeed();
                exitPool();
                address = 0;
            }
        }

        @Override
        protected void returnMemoryIfNeed() {
            if (buffer.isDirect()) {
                super.returnMemoryIfNeed();
            }
        }

        @Override
        public void forceAndClear() {
            forceWrite(true);
            manager.returnMemory(buffer, getLevel());
            allocateSize = 0;
            exitPool();
            manager = null;
        }

        @Override
        public void clear() {
            forceWrite(buffer.isDirect());
        }

        @Override
        protected void loadContent() {
            synchronized (buffer) {
                if (load) {
                    return;
                }
                if (close) {
                    throw new FileCloseException();
                }
                Accessor accessor = null;
                if (directAccess) {
                    accessor = new DirectAccessor(bufferKey);
                } else {
                    accessor = new VirtualAccessor(bufferKey, maxSize << getOffset());
                }
                try {
                    accessor.invoke();
                } catch (BlockNotFoundException e) {
                }
                int off = accessor.getOff();
                byte[] bytes = accessor.getBytes();
                int max_position = off >> getOffset();
                int offset = Maths.log2(max_position);
                if (max_position > (1 << offset)) {
                    offset++;
                }
                int len = 1 << offset << getOffset();
                beforeStatusChange();
                try {
                    address = manager.allocateRead(len);
                    allocateSize = len;
                    MemoryUtils.copyMemory(bytes, address, off);
                    MemoryUtils.fill0(address + off, len - off);
                } catch (OutOfMemoryError error) {
                    //todo 预防内存设置超大 赋值的时候发生溢出需要抛出异常
                    error.printStackTrace();
                }
                load = true;
                this.maxPosition = max_position - 1;
                setCurrentCapacity(offset);
                afterStatusChange();
            }
        }

        @Override
        public boolean isLoad() {
            return load;
        }

        @Override
        public void access() {
            if (!access) {
                access = true;
            }
        }
    }

    private class AtomicClearLong {
        private AtomicLong value = new AtomicLong(0);
        private Watcher watcher;

        public AtomicClearLong(Watcher watcher) {
            this.watcher = watcher;
        }

        void decrement() {
            watcher.watch(value.decrementAndGet());
        }

        void increment() {
            value.incrementAndGet();
        }

        long get() {
            return value.get();
        }

        void set(long value, boolean watch) {
            this.value.set(value);
            if (watch) {
                watcher.watch(value);
            }
        }

        public void decrementWithoutWatch() {
            value.decrementAndGet();
        }
    }
}
