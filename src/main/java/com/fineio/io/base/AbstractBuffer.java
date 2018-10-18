package com.fineio.io.base;

import com.fineio.cache.CacheManager;
import com.fineio.exception.StreamCloseException;
import com.fineio.io.Buffer;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractBuffer implements BaseBuffer {
    private static final int MAX_COUNT = 1024;
    protected final BufferKey bufferKey;
    protected volatile long address;
    protected volatile int max_size;
    protected volatile boolean close;
    protected volatile int allocateSize;
    protected volatile boolean directAccess;
    private volatile AtomicInteger status;
    private volatile boolean access;
    private volatile int waitHelper;
    private transient CacheManager manager;

    protected AbstractBuffer(final Connector connector, final FileBlock fileBlock) {
        this.status = new AtomicInteger(0);
        this.close = false;
        this.access = false;
        this.allocateSize = 0;
        this.directAccess = false;
        this.waitHelper = 0;
        this.bufferKey = new BufferKey(connector, fileBlock);
        (this.manager = CacheManager.getInstance()).registerBuffer((Buffer) this);
    }

    public int getAllocateSize() {
        return this.allocateSize;
    }

    protected final InputStream getInputStream() {
        this.loadContent();
        if (this.address == 0L) {
            throw new StreamCloseException();
        }
        return new DirectInputStream(this.address, this.getByteSize(), new StreamCloseChecker(this.status.get()) {
            @Override
            public boolean check() {
                return AbstractBuffer.this.status.get() == this.getStatus();
            }
        });
    }

    protected abstract void loadContent();

    protected final void access() {
        if (!this.access) {
            this.access = true;
        }
    }

    public boolean recentAccess() {
        return this.access;
    }

    public void resetAccess() {
        this.access = false;
    }

    protected void afterStatusChange() {
        this.status.addAndGet(1);
    }

    protected void beforeStatusChange() {
        this.status.addAndGet(1);
        int count = this.waitHelper + MAX_COUNT;
        while (this.waitHelper++ < count) {
        }
        this.waitHelper = 0;
    }

    public int getByteSize() {
        return this.getLength() << this.getLengthOffset();
    }

    public int getLength() {
        this.loadContent();
        return this.max_size;
    }

    protected final void clearMemory() {
        synchronized (this) {
            this.beforeStatusChange();
            MemoryUtils.free(this.address);
            this.afterStatusChange();
            this.manager.clearBufferMemory((Buffer) this);
        }
    }

    protected final void releaseBuffer() {
        this.manager.releaseBuffer((Buffer) this, this.close);
        if (this.close) {
            this.manager = null;
        }
    }

    protected abstract int getLengthOffset();
}
