package com.fineio.io.edit;

import com.fineio.base.Maths;
import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.FileCloseException;
import com.fineio.io.Buffer;
import com.fineio.io.base.AbstractBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.write.WriteBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.locks.LockSupport;

public abstract class EditBuffer extends WriteBuffer implements Edit {
    private volatile boolean load;

    protected EditBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock, n);
        this.load = false;
    }

    protected EditBuffer(final Connector connector, final URI uri) {
        super(connector, uri);
        this.load = false;
    }

    private final void loadData() {
        synchronized (this) {
            if (this.load) {
                return;
            }
            if (this.close) {
                throw new FileCloseException();
            }
            Accessor accessor;
            if (this.directAccess) {
                accessor = new DirectAccessor();
            } else {
                accessor = new VirtualAccessor();
            }
            accessor.invoke();
            final int off = accessor.getOff();
            final byte[] bytes = accessor.getBytes();
            final int n = off >> this.getLengthOffset();
            int log2 = Maths.log2(n);
            if (n > 1 << log2) {
                ++log2;
            }
            final int allocateSize = 1 << log2 << this.getLengthOffset();
            this.beforeStatusChange();
            try {
                this.address = CacheManager.getInstance().allocateRead((Buffer) this, allocateSize);
                this.allocateSize = allocateSize;
                MemoryUtils.copyMemory(bytes, this.address, off);
                MemoryUtils.fill0(this.address + off, allocateSize - off);
            } catch (OutOfMemoryError outOfMemoryError) {
                outOfMemoryError.printStackTrace();
            }
            this.load = true;
            this.max_position = n - 1;
            this.setCurrentCapacity(log2);
            this.afterStatusChange();
        }
    }

    @Override
    protected void loadContent() {
        this.loadData();
    }

    @Override
    protected void addCapacity() {
        final int n = this.current_max_size << this.getLengthOffset();
        this.setCurrentCapacity(this.current_max_offset + 1);
        final int allocateSize = this.current_max_size << this.getLengthOffset();
        this.beforeStatusChange();
        try {
            this.address = CacheManager.getInstance().allocateEdit((Buffer) this, this.address, n, allocateSize);
            this.allocateSize = allocateSize;
            MemoryUtils.fill0(this.address + n, allocateSize - n);
        } catch (OutOfMemoryError outOfMemoryError) {
            outOfMemoryError.printStackTrace();
        }
        this.afterStatusChange();
    }

    @Override
    protected void ensureCapacity(final int n) {
        if (!this.load) {
            this.loadData();
        }
        if (n < this.max_size) {
            this.addCapacity(n);
            return;
        }
        throw new BufferIndexOutOfBoundsException((long) n);
    }

    @Override
    protected final void checkIndex(final int n) {
        if (this.ir(n)) {
            this.access();
            return;
        }
        this.lc(n);
    }

    private final void lc(final int n) {
        synchronized (this) {
            if (this.load) {
                if (this.ir(n)) {
                    return;
                }
                throw new BufferIndexOutOfBoundsException((long) n);
            } else {
                this.ll(n);
            }
        }
    }

    private final void ll(final int n) {
        this.loadData();
        this.checkIndex(n);
    }

    @Override
    public void force() {
        this.forceWrite();
        this.closeWithOutSync();
    }

    @Override
    public void closeWithOutSync() {
        synchronized (this) {
            if (this.close) {
                return;
            }
            this.close();
            this.cleanMemory();
            this.releaseBuffer();
        }
    }

    @Override
    public LEVEL getLevel() {
        return LEVEL.EDIT;
    }

    @Override
    public void clear() {
        this.forceWrite();
        this.clearAfterWrite();
    }

    private void cleanMemory() {
        synchronized (this) {
            if (!this.load) {
                return;
            }
            this.load = false;
            this.current_max_size = 0;
            this.clearMemory();
        }
    }

    @Override
    protected void clearAfterWrite() {
        synchronized (this) {
            if (!this.load) {
                this.releaseBuffer();
                return;
            }
            this.load = false;
            LockSupport.parkNanos(1000L);
            if (this.needFlush()) {
                this.load = true;
                return;
            }
            this.current_max_size = 0;
            this.clearMemory();
            this.releaseBuffer();
        }
    }

    private class DirectAccessor extends Accessor {
        @Override
        public void invoke() {
            final byte[] array = new byte[1024];
            InputStream read = null;
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                read = EditBuffer.this.bufferKey.getConnector().read(EditBuffer.this.bufferKey.getBlock());
                int read2;
                while ((read2 = read.read(array, 0, array.length)) > 0) {
                    byteArrayOutputStream.write(array, 0, read2);
                }
                this.bytes = byteArrayOutputStream.toByteArray();
                this.off = this.bytes.length;
            } catch (Throwable t) {
            } finally {
                if (read != null) {
                    try {
                        read.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }

    private abstract class Accessor {
        protected byte[] bytes;
        protected int off;

        public byte[] getBytes() {
            return this.bytes;
        }

        public int getOff() {
            return this.off;
        }

        public abstract void invoke();
    }

    private class VirtualAccessor extends Accessor {
        @Override
        public void invoke() {
            final int n = EditBuffer.this.max_size << AbstractBuffer.this.getLengthOffset();
            this.bytes = new byte[n];
            this.off = 0;
            InputStream read = null;
            try {
                read = EditBuffer.this.bufferKey.getConnector().read(EditBuffer.this.bufferKey.getBlock());
                int read2;
                while ((read2 = read.read(this.bytes, this.off, n - this.off)) > 0) {
                    this.off += read2;
                }
            } catch (Throwable t) {
            } finally {
                if (read != null) {
                    try {
                        read.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }
}
