package com.fineio.directio;

import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.AbstractFileModel;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;

import java.net.URI;

public abstract class DirectIOFile<E extends Buffer> {
    protected Connector connector;
    protected URI uri;
    protected volatile E buffer;
    private AbstractFileModel<E> model;
    private volatile boolean released;

    protected DirectIOFile(final Connector connector, final URI uri, final AbstractFileModel<E> model) {
        this.released = false;
        this.connector = connector;
        this.uri = uri;
        this.model = model;
    }

    public static void put(final DirectIOFile<DoubleBuffer> directIOFile, final double n) {
        directIOFile.getBuffer().put(n);
    }

    public static void put(final DirectIOFile<ByteBuffer> directIOFile, final byte b) {
        directIOFile.getBuffer().put(b);
    }

    public static void put(final DirectIOFile<CharBuffer> directIOFile, final char c) {
        directIOFile.getBuffer().put(c);
    }

    public static void put(final DirectIOFile<FloatBuffer> directIOFile, final float n) {
        directIOFile.getBuffer().put(n);
    }

    public static void put(final DirectIOFile<LongBuffer> directIOFile, final long n) {
        directIOFile.getBuffer().put(n);
    }

    public static void put(final DirectIOFile<IntBuffer> directIOFile, final int n) {
        directIOFile.getBuffer().put(n);
    }

    public static void put(final DirectIOFile<ShortBuffer> directIOFile, final short n) {
        directIOFile.getBuffer().put(n);
    }

    public static void put(final DirectIOFile<DoubleBuffer> directIOFile, final int n, final double n2) {
        directIOFile.getBuffer().put(n, n2);
    }

    public static void put(final DirectIOFile<ByteBuffer> directIOFile, final int n, final byte b) {
        directIOFile.getBuffer().put(n, b);
    }

    public static void put(final DirectIOFile<CharBuffer> directIOFile, final int n, final char c) {
        directIOFile.getBuffer().put(n, c);
    }

    public static void put(final DirectIOFile<FloatBuffer> directIOFile, final int n, final float n2) {
        directIOFile.getBuffer().put(n, n2);
    }

    public static void put(final DirectIOFile<LongBuffer> directIOFile, final int n, final long n2) {
        directIOFile.getBuffer().put(n, n2);
    }

    public static void put(final DirectIOFile<IntBuffer> directIOFile, final int n, final int n2) {
        directIOFile.getBuffer().put(n, n2);
    }

    public static void put(final DirectIOFile<ShortBuffer> directIOFile, final int n, final short n2) {
        directIOFile.getBuffer().put(n, n2);
    }

    public static final long getLong(final DirectIOFile<LongBuffer> directIOFile, final int n) {
        return directIOFile.getBuffer().get(n);
    }

    public static final int getInt(final DirectIOFile<IntBuffer> directIOFile, final int n) {
        return directIOFile.getBuffer().get(n);
    }

    public static final char getChar(final DirectIOFile<CharBuffer> directIOFile, final int n) {
        return directIOFile.getBuffer().get(n);
    }

    public static final double getDouble(final DirectIOFile<DoubleBuffer> directIOFile, final int n) {
        return directIOFile.getBuffer().get(n);
    }

    public static final float getFloat(final DirectIOFile<FloatBuffer> directIOFile, final int n) {
        return directIOFile.getBuffer().get(n);
    }

    public static final byte getByte(final DirectIOFile<ByteBuffer> directIOFile, final int n) {
        return directIOFile.getBuffer().get(n);
    }

    public static final short getShort(final DirectIOFile<ShortBuffer> directIOFile, final long n) {
        return directIOFile.getBuffer().get((int) n);
    }

    public String getPath() {
        return this.uri.getPath();
    }
    
    private E getBuffer() {
        return (this.buffer != null) ? this.buffer : this.initBuffer();
    }

    public int length() {
        return this.getBuffer().getLength();
    }

    public int byteLength() {
        return this.getBuffer().getByteSize();
    }

    protected E initBuffer() {
        if (this.buffer == null) {
            synchronized (this) {
                this.buffer = this.model.createBuffer(this.connector, this.uri);
            }
        }
        return this.buffer;
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
            if (this.buffer != null) {
                this.buffer.force();
            }
            this.released = true;
        }
    }

    public boolean delete() {
        synchronized (this) {
            if (!this.released) {
                if (this.buffer != null) {
                    this.buffer.closeWithOutSync();
                    this.buffer = null;
                }
                this.released = true;
            }
        }
        return this.connector.delete(new FileBlock(this.uri));
    }
}
