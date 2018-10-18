package com.fineio.io.read;

import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.FileCloseException;
import com.fineio.io.Buffer;
import com.fineio.io.base.AbstractBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public abstract class ReadBuffer extends AbstractBuffer implements Read {
    protected int max_byte_len;
    private volatile boolean load;

    protected ReadBuffer(final Connector connector, final FileBlock fileBlock, final int n) {
        super(connector, fileBlock);
        this.load = false;
        this.max_byte_len = 1 << n + this.getLengthOffset();
        this.directAccess = false;
    }

    protected ReadBuffer(final Connector connector, final URI uri) {
        super(connector, new FileBlock(uri));
        this.load = false;
        this.directAccess = true;
    }

    public void put(final int n, final byte b) {
        this.put(b);
    }

    public void put(final int n, final int n2) {
        this.put(n2);
    }

    public void put(final int n, final double n2) {
        this.put(n2);
    }

    public void put(final int n, final long n2) {
        this.put(n2);
    }

    public void put(final int n, final char c) {
        this.put(c);
    }

    public void put(final int n, final short n2) {
        this.put(n2);
    }

    public void put(final int n, final float n2) {
        this.put(n2);
    }

    public void put(final byte b) {
        this.unSupport();
    }

    private void unSupport() {
        throw new UnsupportedOperationException(this.getClass().getName() + " put");
    }

    public void put(final int n) {
        this.unSupport();
    }

    public void put(final double n) {
        this.unSupport();
    }

    public void put(final long n) {
        this.unSupport();
    }

    public void put(final char c) {
        this.unSupport();
    }

    public void put(final short n) {
        this.unSupport();
    }

    public void put(final float n) {
        this.unSupport();
    }

    public void write() {
        this.unSupport();
    }

    public LEVEL getLevel() {
        return LEVEL.READ;
    }

    @Override
    protected void loadContent() {
        this.loadData();
    }

    private final void loadData() {
        synchronized (this) {
            if (this.load) {
                return;
            }
            if (this.close) {
                throw new FileCloseException();
            }
            if (this.directAccess) {
                this.DirectAccess();
            } else {
                this.VirtualAccess();
            }
        }
    }

    private void DirectAccess() {
        InputStream read = null;
        try {
            read = this.bufferKey.getConnector().read(this.bufferKey.getBlock());
            if (read == null) {
                throw new BlockNotFoundException("block:" + this.bufferKey.getBlock().toString() + " not found!");
            }
            final byte[] array = new byte[1024];
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read2;
            while ((read2 = read.read(array, 0, array.length)) > 0) {
                byteArrayOutputStream.write(array, 0, read2);
            }
            final byte[] byteArray = byteArrayOutputStream.toByteArray();
            this.allocateMemory(byteArray, byteArray.length);
        } catch (IOException ex) {
            throw new BlockNotFoundException("block:" + this.bufferKey.getBlock().toString() + " not found!");
        } catch (OutOfMemoryError outOfMemoryError) {
            outOfMemoryError.printStackTrace();
        } finally {
            if (read != null) {
                try {
                    read.close();
                } catch (IOException ex2) {
                }
            }
        }
    }

    private void allocateMemory(final byte[] array, final int allocateSize) {
        this.beforeStatusChange();
        MemoryUtils.copyMemory(array, this.address = CacheManager.getInstance().allocateRead((Buffer) this, allocateSize), allocateSize);
        this.allocateSize = allocateSize;
        this.load = true;
        this.max_size = allocateSize >> this.getLengthOffset();
        this.afterStatusChange();
    }

    private void VirtualAccess() {
        InputStream read = null;
        try {
            read = this.bufferKey.getConnector().read(this.bufferKey.getBlock());
            if (read == null) {
                throw new BlockNotFoundException("block:" + this.bufferKey.getBlock().toString() + " not found!");
            }
            byte[] array;
            int n;
            int read2;
            for (array = new byte[this.max_byte_len], n = 0; (read2 = read.read(array, n, this.max_byte_len - n)) > 0; n += read2) {
            }
            this.allocateMemory(array, n);
        } catch (IOException ex) {
            throw new BlockNotFoundException("block:" + this.bufferKey.getBlock().toString() + " not found!");
        } catch (OutOfMemoryError outOfMemoryError) {
            outOfMemoryError.printStackTrace();
        } finally {
            if (read != null) {
                try {
                    read.close();
                } catch (IOException ex2) {
                }
            }
        }
    }

    public boolean full() {
        return this.max_byte_len >> this.getLengthOffset() == this.max_size;
    }

    protected final void checkIndex(final int n) {
        if (this.ir(n)) {
            this.access();
            return;
        }
        this.lc(n);
    }

    private final boolean ir(final int n) {
        return n > -1 && n < this.max_size;
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
    public void clear() {
        synchronized (this) {
            if (!this.load) {
                return;
            }
            this.load = false;
            this.max_size = 0;
            this.clearMemory();
            this.releaseBuffer();
        }
    }

    public void force() {
        this.closeWithOutSync();
    }

    public void closeWithOutSync() {
        synchronized (this) {
            if (this.close) {
                return;
            }
            this.close = true;
            this.clear();
        }
    }
}
