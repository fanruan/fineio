package com.fineio.io.write;

import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.StreamCloseException;
import com.fineio.io.Buffer;
import com.fineio.io.base.AbstractBuffer;
import com.fineio.io.base.Job;
import com.fineio.io.base.JobAssist;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.writer.SyncManager;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.net.URI;

public abstract class WriteBuffer extends AbstractBuffer implements Write {
    public static final int DEFAULT_CAPACITY_OFFSET = 10;
    private static volatile long PERIOD;

    static {
        WriteBuffer.PERIOD = 20000L;
    }

    protected int current_max_size;
    protected int current_max_offset;
    protected int max_offset;
    protected int max_position;
    protected volatile boolean flushed;
    protected volatile boolean changed;
    private transient long lastWriteTime;

    protected WriteBuffer(final Connector connector, final FileBlock fileBlock, final int max_offset) {
        super(connector, fileBlock);
        this.current_max_offset = 10;
        this.max_position = -1;
        this.flushed = false;
        this.changed = false;
        this.max_offset = max_offset;
        this.max_size = 1 << max_offset;
        this.directAccess = false;
    }

    protected WriteBuffer(final Connector connector, final URI uri) {
        super(connector, new FileBlock(uri));
        this.current_max_offset = 10;
        this.max_position = -1;
        this.flushed = false;
        this.changed = false;
        this.max_offset = 31;
        this.max_size = Integer.MAX_VALUE;
        this.directAccess = true;
    }

    public boolean hasChanged() {
        return this.changed;
    }

    public boolean needFlush() {
        return !this.flushed || this.changed;
    }

    protected void checkIndex(final int n) {
        if (this.ir(n)) {
            return;
        }
        throw new BufferIndexOutOfBoundsException((long) n);
    }

    public boolean full() {
        return this.max_position == this.max_size - 1;
    }

    @Override
    public final int getByteSize() {
        return this.getLength() << this.getLengthOffset();
    }

    @Override
    public int getLength() {
        return this.max_position + 1;
    }

    @Override
    protected void loadContent() {
    }

    protected final boolean ir(final int n) {
        return n > -1 && n < this.current_max_size;
    }

    protected final void setCurrentCapacity(final int current_max_offset) {
        this.current_max_offset = current_max_offset;
        this.current_max_size = 1 << current_max_offset;
    }

    protected void ensureCapacity(final int n) {
        if (n < this.max_size) {
            this.addCapacity(n);
            this.changed = true;
            return;
        }
        throw new BufferIndexOutOfBoundsException((long) n);
    }

    private final void setMaxPosition(final int max_position) {
        this.access();
        if (max_position > this.max_position) {
            this.max_position = max_position;
        }
    }

    protected final void addCapacity(final int i) {
        while (i >= this.current_max_size) {
            this.addCapacity();
        }
        this.setMaxPosition(i);
    }

    protected void addCapacity() {
        final int n = this.current_max_size << this.getLengthOffset();
        this.setCurrentCapacity(this.current_max_offset + 1);
        final int allocateSize = this.current_max_size << this.getLengthOffset();
        this.beforeStatusChange();
        try {
            this.address = CacheManager.getInstance().allocateWrite((Buffer) this, this.address, n, allocateSize);
            this.allocateSize = allocateSize;
            MemoryUtils.fill0(this.address + n, allocateSize - n);
        } catch (OutOfMemoryError outOfMemoryError) {
            outOfMemoryError.printStackTrace();
        }
        this.afterStatusChange();
    }

    public void force() {
        this.forceWrite();
        this.closeWithOutSync();
    }

    public void closeWithOutSync() {
        this.clear();
    }

    protected final void forceWrite() {
        int n = 0;
        while (this.needFlush()) {
            ++n;
            SyncManager.getInstance().force(this.createWriteJob());
            if (n > 3) {
                this.flushed = true;
                break;
            }
        }
    }

    protected JobAssist createWriteJob() {
        return new JobAssist(this.bufferKey, new Job() {
            @Override
            public void doJob() {
                try {
                    WriteBuffer.this.write0();
                } catch (StreamCloseException ex) {
                    WriteBuffer.this.flushed = false;
                    this.doJob();
                }
            }
        });
    }

    public void write() {
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastWriteTime > WriteBuffer.PERIOD) {
            this.lastWriteTime = currentTimeMillis;
            SyncManager.getInstance().triggerWork(this.createWriteJob());
        }
    }

    protected void close() {
        this.close = true;
        this.max_size = 0;
    }

    @Override
    public void clear() {
        synchronized (this) {
            if (this.close) {
                return;
            }
            this.close();
            this.current_max_size = 0;
            this.clearMemory();
            this.releaseBuffer();
        }
    }

    protected void clearAfterWrite() {
        this.clear();
    }

    protected void write0() {
        synchronized (this) {
            this.changed = false;
            try {
                this.bufferKey.getConnector().write(this.bufferKey.getBlock(), this.getInputStream());
                this.flushed = true;
                this.clearAfterWrite();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public LEVEL getLevel() {
        return LEVEL.WRITE;
    }
}
