package com.fineio.io.file;

import com.fineio.base.Bits;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.exception.IOSetException;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.net.URI;

public abstract class IOFile<E extends Buffer> {
    protected URI uri;
    protected Connector connector;
    protected int blocks;
    protected byte block_size_offset;
    private static final int HEAD_LEN = 9;
    protected long single_block_len;
    protected volatile E[] buffers;
    private AbstractFileModel<E> model;
    private volatile boolean released;
    private volatile int bufferWriteIndex;

    IOFile(final Connector connector, final URI uri, final AbstractFileModel<E> model) {
        this.released = false;
        this.bufferWriteIndex = -1;
        if (uri == null || connector == null || model == null) {
            throw new IOSetException("uri  or connector or model can't be null");
        }
        this.connector = connector;
        this.uri = URI.create(uri.getPath() + "/");
        this.model = model;
    }

    public static void put(final IOFile<DoubleBuffer> ioFile, final double n) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.gi())).put(n);
    }

    public static void put(final IOFile<ByteBuffer> ioFile, final byte b) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.gi())).put(b);
    }

    public static void put(final IOFile<CharBuffer> ioFile, final char c) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.gi())).put(c);
    }

    public static void put(final IOFile<FloatBuffer> ioFile, final float n) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.gi())).put(n);
    }

    public static void put(final IOFile<LongBuffer> ioFile, final long n) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.gi())).put(n);
    }

    public static void put(final IOFile<IntBuffer> ioFile, final int n) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.gi())).put(n);
    }

    public static void put(final IOFile<ShortBuffer> ioFile, final short n) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.gi())).put(n);
    }

    public static void put(final IOFile<DoubleBuffer> ioFile, final long n, final double n2) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.giw(n))).put(ioFile.gp(n), n2);
    }

    public static void put(final IOFile<ByteBuffer> ioFile, final long n, final byte b) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.giw(n))).put(ioFile.gp(n), b);
    }

    public static void put(final IOFile<CharBuffer> ioFile, final long n, final char c) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.giw(n))).put(ioFile.gp(n), c);
    }

    public static void put(final IOFile<FloatBuffer> ioFile, final long n, final float n2) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.giw(n))).put(ioFile.gp(n), n2);
    }

    public static void put(final IOFile<LongBuffer> ioFile, final long n, final long n2) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.giw(n))).put(ioFile.gp(n), n2);
    }

    public static void put(final IOFile<IntBuffer> ioFile, final long n, final int n2) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.giw(n))).put(ioFile.gp(n), n2);
    }

    public static void put(final IOFile<ShortBuffer> ioFile, final long n, final short n2) {
        ioFile.getBuffer(ioFile.checkBuffer(ioFile.giw(n))).put(ioFile.gp(n), n2);
    }

    public static final long getLong(final IOFile<LongBuffer> ioFile, final long n) {
        return ioFile.getBuffer(ioFile.gi(n)).get(ioFile.gp(n));
    }

    public static final int getInt(final IOFile<IntBuffer> ioFile, final long n) {
        return ioFile.getBuffer(ioFile.gi(n)).get(ioFile.gp(n));
    }

    public static final char getChar(final IOFile<CharBuffer> ioFile, final long n) {
        return ioFile.getBuffer(ioFile.gi(n)).get(ioFile.gp(n));
    }

    public static final double getDouble(final IOFile<DoubleBuffer> ioFile, final long n) {
        return ioFile.getBuffer(ioFile.gi(n)).get(ioFile.gp(n));
    }

    public static final float getFloat(final IOFile<FloatBuffer> ioFile, final long n) {
        return ioFile.getBuffer(ioFile.gi(n)).get(ioFile.gp(n));
    }

    public static final byte getByte(final IOFile<ByteBuffer> ioFile, final long n) {
        return ioFile.getBuffer(ioFile.gi(n)).get(ioFile.gp(n));
    }

    public static final short getShort(final IOFile<ShortBuffer> ioFile, final long n) {
        return ioFile.getBuffer(ioFile.gi(n)).get(ioFile.gp(n));
    }

    public String getPath() {
        return this.uri.getPath();
    }

    protected final FileBlock createHeadBlock() {
        return new FileBlock(this.uri, "head");
    }

    protected final void createBufferArray(final int blocks) {
        this.blocks = blocks;
        this.buffers = new Buffer[blocks];
    }

    private boolean inRange(final int n) {
        return this.buffers != null && this.buffers.length > n;
    }

    protected final int checkBuffer(final int n) {
        if (n < 0) {
            throw new BufferIndexOutOfBoundsException((long) n);
        }
        return this.inRange(n) ? n : this.createBufferArrayInRange(n);
    }

    private int createBufferArrayInRange(final int n) {
        final Buffer[] buffers = this.buffers;
        this.createBufferArray(n + 1);
        if (buffers != null) {
            System.arraycopy(buffers, 0, this.buffers, 0, buffers.length);
        }
        return n;
    }

    protected int gi(final long n) {
        return (int) (n >> this.block_size_offset);
    }

    protected int giw(final long n) {
        final int n2 = (int) (n >> this.block_size_offset);
        if (n2 > 0) {
            this.checkWrite(n2);
        }
        return n2;
    }

    protected void checkWrite(final int bufferWriteIndex) {
        if (this.bufferWriteIndex != bufferWriteIndex) {
            if (this.bufferWriteIndex != -1) {
                this.buffers[this.bufferWriteIndex].write();
            }
            this.bufferWriteIndex = bufferWriteIndex;
        }
    }

    private final int gi() {
        if (this.buffers == null || this.buffers.length == 0) {
            return 0;
        }
        final int n = this.buffers.length - 1;
        return this.buffers[n].full() ? this.triggerWrite(n + 1) : n;
    }

    private final int triggerWrite(final int n) {
        this.checkWrite(n);
        return n;
    }

    private final int gp(final long n) {
        return (int) (n & this.single_block_len);
    }

    private final E getBuffer(final int n) {
        return ((this.buffers[this.checkIndex(n)] != null) ? this.buffers[n] : this.initBuffer(n));
    }

    private int checkIndex(final int n) {
        if (n > -1 && n < this.blocks) {
            return n;
        }
        throw new BufferIndexOutOfBoundsException((long) n);
    }

    private E initBuffer(final int n) {
        synchronized (this) {
            if (this.buffers[n] == null) {
                this.buffers[n] = this.createBuffer(n);
            }
            return this.buffers[n];
        }
    }

    public boolean delete() {
        synchronized (this) {
            boolean delete = this.connector.delete(this.createHeadBlock());
            if (this.buffers != null) {
                for (int i = 0; i < this.buffers.length; ++i) {
                    if (!this.released && this.buffers[i] != null) {
                        this.buffers[i].closeWithOutSync();
                        this.buffers[i] = null;
                    }
                    final boolean delete2 = this.connector.delete(this.createIndexBlock(i));
                    if (delete) {
                        delete = delete2;
                    }
                }
            }
            final boolean delete3 = this.connector.delete(new FileBlock(this.uri));
            if (delete) {
                delete = delete3;
            }
            this.released = true;
            return delete;
        }
    }

    public boolean copyTo(final URI uri) {
        synchronized (this) {
            try {
                if (this.buffers != null) {
                    final URI create = URI.create(uri.getPath() + "/");
                    this.connector.copy(this.createHeadBlock(), new FileBlock(create, "head"));
                    for (int i = 0; i < this.buffers.length; ++i) {
                        this.connector.copy(this.createIndexBlock(i), new FileBlock(create, String.valueOf(i)));
                    }
                    return true;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    
    public boolean exists() {
        synchronized (this) {
            boolean exists = this.connector.exists(this.createHeadBlock());
            final boolean exists2 = this.connector.exists(this.createIndexBlock(0));
            if (exists) {
                exists = exists2;
            }
            this.released = true;
            return exists;
        }
    }

    private final FileBlock createIndexBlock(final int n) {
        return new FileBlock(this.uri, String.valueOf(n));
    }

    private E createBuffer(final int n) {
        return this.model.createBuffer(this.connector, this.createIndexBlock(n), this.block_size_offset);
    }

    protected void writeHeader() {
        final FileBlock headBlock = this.createHeadBlock();
        final byte[] array = new byte[9];
        Bits.putInt(array, 0, (this.buffers == null) ? 0 : this.buffers.length);
        array[8] = (byte) (this.block_size_offset + this.model.offset());
        try {
            this.connector.write(headBlock, array);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    protected void finalize() {
        this.close();
    }
    
    public void close() {
        synchronized (this) {
            if (this.released) {
                return;
            }
            this.writeHeader();
            if (this.buffers != null) {
                for (int i = 0; i < this.buffers.length; ++i) {
                    if (this.buffers[i] != null) {
                        this.buffers[i].force();
                        this.buffers[i] = null;
                    }
                }
            }
            this.released = true;
        }
    }
}
