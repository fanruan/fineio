package com.fineio;

import com.fineio.cache.CacheManager;
import com.fineio.directio.DirectEditIOFile;
import com.fineio.directio.DirectIOFile;
import com.fineio.directio.DirectReadIOFile;
import com.fineio.directio.DirectWriteIOFile;
import com.fineio.exception.MemorySetException;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.EditIOFile;
import com.fineio.io.file.IOFile;
import com.fineio.io.file.ReadIOFile;
import com.fineio.io.file.WriteIOFile;
import com.fineio.io.file.writer.SyncManager;
import com.fineio.memory.MemoryConf;
import com.fineio.storage.Connector;

import java.net.URI;

public final class FineIO {
    public static <F> F createIOFile(final Connector connector, final URI uri, final MODEL<F> model) {
        return model.createIOFile(connector, uri);
    }

    public static long getTotalMemSize() {
        return MemoryConf.getTotalMemSize();
    }

    public static void setTotalMemSize(final long totalMemSize) throws MemorySetException {
        MemoryConf.setTotalMemSize(totalMemSize);
    }

    public static final long getMaxMemSizeForSet() {
        return MemoryConf.getMaxMemSizeForSet();
    }

    public static final long getMinMemSizeForSet() {
        return MemoryConf.getMinMemSizeForSet();
    }

    public static final void setMemoryCheckSchedule(final long n) {
        CacheManager.getInstance().resetTimer(n);
    }

    public static int getSyncThreads() {
        return SyncManager.getInstance().getThreads();
    }

    public static void setSyncThreads(final int threads) {
        SyncManager.getInstance().setThreads(threads);
    }

    public static long getFreeMemory() {
        return MemoryConf.getFreeMemory();
    }

    public static long getCurrentMemorySize() {
        return CacheManager.getInstance().getCurrentMemorySize();
    }

    public static long getCurrentReadMemorySize() {
        return CacheManager.getInstance().getReadSize();
    }

    public static long getCurrentWriteMemorySize() {
        return CacheManager.getInstance().getWriteSize();
    }

    public static long getReadWaitCount() {
        return CacheManager.getInstance().getReadWaitCount();
    }

    public static long getWriteWaitCount() {
        return CacheManager.getInstance().getWriteWaitCount();
    }

    public static void put(final IOFile<DoubleBuffer> ioFile, final long n, final double n2) {
        IOFile.put(ioFile, n, n2);
    }

    public static void put(final IOFile<ByteBuffer> ioFile, final long n, final byte b) {
        IOFile.put(ioFile, n, b);
    }

    public static void put(final IOFile<CharBuffer> ioFile, final long n, final char c) {
        IOFile.put(ioFile, n, c);
    }

    public static void put(final IOFile<FloatBuffer> ioFile, final long n, final float n2) {
        IOFile.put(ioFile, n, n2);
    }

    public static void put(final IOFile<LongBuffer> ioFile, final long n, final long n2) {
        IOFile.put(ioFile, n, n2);
    }

    public static void put(final IOFile<IntBuffer> ioFile, final long n, final int n2) {
        IOFile.put(ioFile, n, n2);
    }

    public static void put(final IOFile<ShortBuffer> ioFile, final long n, final short n2) {
        IOFile.put(ioFile, n, n2);
    }

    public static void put(final IOFile<DoubleBuffer> ioFile, final double n) {
        IOFile.put(ioFile, n);
    }

    public static void put(final IOFile<ByteBuffer> ioFile, final byte b) {
        IOFile.put(ioFile, b);
    }

    public static void put(final IOFile<CharBuffer> ioFile, final char c) {
        IOFile.put(ioFile, c);
    }

    public static void put(final IOFile<FloatBuffer> ioFile, final float n) {
        IOFile.put(ioFile, n);
    }

    public static void put(final IOFile<LongBuffer> ioFile, final long n) {
        IOFile.put(ioFile, n);
    }

    public static void put(final IOFile<IntBuffer> ioFile, final int n) {
        IOFile.put(ioFile, n);
    }

    public static void put(final IOFile<ShortBuffer> ioFile, final short n) {
        IOFile.put(ioFile, n);
    }

    public static final long getLong(final IOFile<LongBuffer> ioFile, final long n) {
        return IOFile.getLong(ioFile, n);
    }

    public static final int getInt(final IOFile<IntBuffer> ioFile, final long n) {
        return IOFile.getInt(ioFile, n);
    }

    public static final char getChar(final IOFile<CharBuffer> ioFile, final long n) {
        return IOFile.getChar(ioFile, n);
    }

    public static final double getDouble(final IOFile<DoubleBuffer> ioFile, final long n) {
        return IOFile.getDouble(ioFile, n);
    }

    public static final float getFloat(final IOFile<FloatBuffer> ioFile, final long n) {
        return IOFile.getFloat(ioFile, n);
    }

    public static final byte getByte(final IOFile<ByteBuffer> ioFile, final long n) {
        return IOFile.getByte(ioFile, n);
    }

    public static final short getShort(final IOFile<ShortBuffer> ioFile, final long n) {
        return IOFile.getShort(ioFile, n);
    }

    public static void put(final DirectIOFile<DoubleBuffer> directIOFile, final int n, final double n2) {
        DirectIOFile.put(directIOFile, n, n2);
    }

    public static void put(final DirectIOFile<ByteBuffer> directIOFile, final int n, final byte b) {
        DirectIOFile.put(directIOFile, n, b);
    }

    public static void put(final DirectIOFile<CharBuffer> directIOFile, final int n, final char c) {
        DirectIOFile.put(directIOFile, n, c);
    }

    public static void put(final DirectIOFile<FloatBuffer> directIOFile, final int n, final float n2) {
        DirectIOFile.put(directIOFile, n, n2);
    }

    public static void put(final DirectIOFile<LongBuffer> directIOFile, final int n, final long n2) {
        DirectIOFile.put(directIOFile, n, n2);
    }

    public static void put(final DirectIOFile<IntBuffer> directIOFile, final int n, final int n2) {
        DirectIOFile.put(directIOFile, n, n2);
    }

    public static void put(final DirectIOFile<ShortBuffer> directIOFile, final int n, final short n2) {
        DirectIOFile.put(directIOFile, n, n2);
    }

    public static void put(final DirectIOFile<DoubleBuffer> directIOFile, final double n) {
        DirectIOFile.put(directIOFile, n);
    }

    public static void put(final DirectIOFile<ByteBuffer> directIOFile, final byte b) {
        DirectIOFile.put(directIOFile, b);
    }

    public static void put(final DirectIOFile<CharBuffer> directIOFile, final char c) {
        DirectIOFile.put(directIOFile, c);
    }

    public static void put(final DirectIOFile<FloatBuffer> directIOFile, final float n) {
        DirectIOFile.put(directIOFile, n);
    }

    public static void put(final DirectIOFile<LongBuffer> directIOFile, final long n) {
        DirectIOFile.put(directIOFile, n);
    }

    public static void put(final DirectIOFile<IntBuffer> directIOFile, final int n) {
        DirectIOFile.put(directIOFile, n);
    }

    public static void put(final DirectIOFile<ShortBuffer> directIOFile, final short n) {
        DirectIOFile.put(directIOFile, n);
    }

    public static final long getLong(final DirectIOFile<LongBuffer> directIOFile, final int n) {
        return DirectIOFile.getLong(directIOFile, n);
    }

    public static final int getInt(final DirectIOFile<IntBuffer> directIOFile, final int n) {
        return DirectIOFile.getInt(directIOFile, n);
    }

    public static final char getChar(final DirectIOFile<CharBuffer> directIOFile, final int n) {
        return DirectIOFile.getChar(directIOFile, n);
    }

    public static final double getDouble(final DirectIOFile<DoubleBuffer> directIOFile, final int n) {
        return DirectIOFile.getDouble(directIOFile, n);
    }

    public static final float getFloat(final DirectIOFile<FloatBuffer> directIOFile, final int n) {
        return DirectIOFile.getFloat(directIOFile, n);
    }

    public static final byte getByte(final DirectIOFile<ByteBuffer> directIOFile, final int n) {
        return DirectIOFile.getByte(directIOFile, n);
    }

    public static final short getShort(final DirectIOFile<ShortBuffer> directIOFile, final int n) {
        return DirectIOFile.getShort(directIOFile, n);
    }

    public interface MODEL<F> {
        MODEL<ReadIOFile<LongBuffer>> READ_LONG = new MODEL<ReadIOFile<LongBuffer>>() {
            @Override
            public ReadIOFile<LongBuffer> createIOFile(final Connector connector, final URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.LONG);
            }
        };
        MODEL<ReadIOFile<DoubleBuffer>> READ_DOUBLE = new MODEL<ReadIOFile<DoubleBuffer>>() {
            @Override
            public ReadIOFile<DoubleBuffer> createIOFile(final Connector connector, final URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.DOUBLE);
            }
        };
        MODEL<ReadIOFile<FloatBuffer>> READ_FLOAT = new MODEL<ReadIOFile<FloatBuffer>>() {
            @Override
            public ReadIOFile<FloatBuffer> createIOFile(final Connector connector, final URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.FLOAT);
            }
        };
        MODEL<ReadIOFile<IntBuffer>> READ_INT = new MODEL<ReadIOFile<IntBuffer>>() {
            @Override
            public ReadIOFile<IntBuffer> createIOFile(final Connector connector, final URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.INT);
            }
        };
        MODEL<ReadIOFile<CharBuffer>> READ_CHAR = new MODEL<ReadIOFile<CharBuffer>>() {
            @Override
            public ReadIOFile<CharBuffer> createIOFile(final Connector connector, final URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.CHAR);
            }
        };
        MODEL<ReadIOFile<ByteBuffer>> READ_BYTE = new MODEL<ReadIOFile<ByteBuffer>>() {
            @Override
            public ReadIOFile<ByteBuffer> createIOFile(final Connector connector, final URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.BYTE);
            }
        };
        MODEL<ReadIOFile<ShortBuffer>> READ_SHORT = new MODEL<ReadIOFile<ShortBuffer>>() {
            @Override
            public ReadIOFile<ShortBuffer> createIOFile(final Connector connector, final URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.SHORT);
            }
        };
        MODEL<WriteIOFile<LongBuffer>> WRITE_LONG = new MODEL<WriteIOFile<LongBuffer>>() {
            @Override
            public WriteIOFile<LongBuffer> createIOFile(final Connector connector, final URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.LONG);
            }
        };
        MODEL<WriteIOFile<DoubleBuffer>> WRITE_DOUBLE = new MODEL<WriteIOFile<DoubleBuffer>>() {
            @Override
            public WriteIOFile<DoubleBuffer> createIOFile(final Connector connector, final URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.DOUBLE);
            }
        };
        MODEL<WriteIOFile<FloatBuffer>> WRITE_FLOAT = new MODEL<WriteIOFile<FloatBuffer>>() {
            @Override
            public WriteIOFile<FloatBuffer> createIOFile(final Connector connector, final URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.FLOAT);
            }
        };
        MODEL<WriteIOFile<IntBuffer>> WRITE_INT = new MODEL<WriteIOFile<IntBuffer>>() {
            @Override
            public WriteIOFile<IntBuffer> createIOFile(final Connector connector, final URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.INT);
            }
        };
        MODEL<WriteIOFile<CharBuffer>> WRITE_CHAR = new MODEL<WriteIOFile<CharBuffer>>() {
            @Override
            public WriteIOFile<CharBuffer> createIOFile(final Connector connector, final URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.CHAR);
            }
        };
        MODEL<WriteIOFile<ByteBuffer>> WRITE_BYTE = new MODEL<WriteIOFile<ByteBuffer>>() {
            @Override
            public WriteIOFile<ByteBuffer> createIOFile(final Connector connector, final URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.BYTE);
            }
        };
        MODEL<WriteIOFile<ShortBuffer>> WRITE_SHORT = new MODEL<WriteIOFile<ShortBuffer>>() {
            @Override
            public WriteIOFile<ShortBuffer> createIOFile(final Connector connector, final URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.SHORT);
            }
        };
        MODEL<EditIOFile<LongBuffer>> EDIT_LONG = new MODEL<EditIOFile<LongBuffer>>() {
            @Override
            public EditIOFile<LongBuffer> createIOFile(final Connector connector, final URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.LONG);
            }
        };
        MODEL<EditIOFile<DoubleBuffer>> EDIT_DOUBLE = new MODEL<EditIOFile<DoubleBuffer>>() {
            @Override
            public EditIOFile<DoubleBuffer> createIOFile(final Connector connector, final URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.DOUBLE);
            }
        };
        MODEL<EditIOFile<FloatBuffer>> EDIT_FLOAT = new MODEL<EditIOFile<FloatBuffer>>() {
            @Override
            public EditIOFile<FloatBuffer> createIOFile(final Connector connector, final URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.FLOAT);
            }
        };
        MODEL<EditIOFile<IntBuffer>> EDIT_INT = new MODEL<EditIOFile<IntBuffer>>() {
            @Override
            public EditIOFile<IntBuffer> createIOFile(final Connector connector, final URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.INT);
            }
        };
        MODEL<EditIOFile<CharBuffer>> EDIT_CHAR = new MODEL<EditIOFile<CharBuffer>>() {
            @Override
            public EditIOFile<CharBuffer> createIOFile(final Connector connector, final URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.CHAR);
            }
        };
        MODEL<EditIOFile<ByteBuffer>> EDIT_BYTE = new MODEL<EditIOFile<ByteBuffer>>() {
            @Override
            public EditIOFile<ByteBuffer> createIOFile(final Connector connector, final URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.BYTE);
            }
        };
        MODEL<EditIOFile<ShortBuffer>> EDIT_SHORT = new MODEL<EditIOFile<ShortBuffer>>() {
            @Override
            public EditIOFile<ShortBuffer> createIOFile(final Connector connector, final URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.SHORT);
            }
        };
        MODEL<DirectReadIOFile<LongBuffer>> READ_LONG_DIRECT = new MODEL<DirectReadIOFile<LongBuffer>>() {
            @Override
            public DirectReadIOFile<LongBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, DirectReadIOFile.LONG);
            }
        };
        MODEL<DirectReadIOFile<DoubleBuffer>> READ_DOUBLE_DIRECT = new MODEL<DirectReadIOFile<DoubleBuffer>>() {
            @Override
            public DirectReadIOFile<DoubleBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, DirectReadIOFile.DOUBLE);
            }
        };
        MODEL<DirectReadIOFile<FloatBuffer>> READ_FLOAT_DIRECT = new MODEL<DirectReadIOFile<FloatBuffer>>() {
            @Override
            public DirectReadIOFile<FloatBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, DirectReadIOFile.FLOAT);
            }
        };
        MODEL<DirectReadIOFile<IntBuffer>> READ_INT_DIRECT = new MODEL<DirectReadIOFile<IntBuffer>>() {
            @Override
            public DirectReadIOFile<IntBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, DirectReadIOFile.INT);
            }
        };
        MODEL<DirectReadIOFile<CharBuffer>> READ_CHAR_DIRECT = new MODEL<DirectReadIOFile<CharBuffer>>() {
            @Override
            public DirectReadIOFile<CharBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, DirectReadIOFile.CHAR);
            }
        };
        MODEL<DirectReadIOFile<ByteBuffer>> READ_BYTE_DIRECT = new MODEL<DirectReadIOFile<ByteBuffer>>() {
            @Override
            public DirectReadIOFile<ByteBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, DirectReadIOFile.BYTE);
            }
        };
        MODEL<DirectReadIOFile<ShortBuffer>> READ_SHORT_DIRECT = new MODEL<DirectReadIOFile<ShortBuffer>>() {
            @Override
            public DirectReadIOFile<ShortBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, DirectReadIOFile.SHORT);
            }
        };
        MODEL<DirectWriteIOFile<LongBuffer>> WRITE_LONG_DIRECT = new MODEL<DirectWriteIOFile<LongBuffer>>() {
            @Override
            public DirectWriteIOFile<LongBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, DirectWriteIOFile.LONG);
            }
        };
        MODEL<DirectWriteIOFile<DoubleBuffer>> WRITE_DOUBLE_DIRECT = new MODEL<DirectWriteIOFile<DoubleBuffer>>() {
            @Override
            public DirectWriteIOFile<DoubleBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, DirectWriteIOFile.DOUBLE);
            }
        };
        MODEL<DirectWriteIOFile<FloatBuffer>> WRITE_FLOAT_DIRECT = new MODEL<DirectWriteIOFile<FloatBuffer>>() {
            @Override
            public DirectWriteIOFile<FloatBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, DirectWriteIOFile.FLOAT);
            }
        };
        MODEL<DirectWriteIOFile<IntBuffer>> WRITE_INT_DIRECT = new MODEL<DirectWriteIOFile<IntBuffer>>() {
            @Override
            public DirectWriteIOFile<IntBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, DirectWriteIOFile.INT);
            }
        };
        MODEL<DirectWriteIOFile<CharBuffer>> WRITE_CHAR_DIRECT = new MODEL<DirectWriteIOFile<CharBuffer>>() {
            @Override
            public DirectWriteIOFile<CharBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, DirectWriteIOFile.CHAR);
            }
        };
        MODEL<DirectWriteIOFile<ByteBuffer>> WRITE_BYTE_DIRECT = new MODEL<DirectWriteIOFile<ByteBuffer>>() {
            @Override
            public DirectWriteIOFile<ByteBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, DirectWriteIOFile.BYTE);
            }
        };
        MODEL<DirectWriteIOFile<ShortBuffer>> WRITE_SHORT_DIRECT = new MODEL<DirectWriteIOFile<ShortBuffer>>() {
            @Override
            public DirectWriteIOFile<ShortBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, DirectWriteIOFile.SHORT);
            }
        };
        MODEL<DirectEditIOFile<LongBuffer>> EDIT_LONG_DIRECT = new MODEL<DirectEditIOFile<LongBuffer>>() {
            @Override
            public DirectEditIOFile<LongBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, DirectEditIOFile.LONG);
            }
        };
        MODEL<DirectEditIOFile<DoubleBuffer>> EDIT_DOUBLE_DIRECT = new MODEL<DirectEditIOFile<DoubleBuffer>>() {
            @Override
            public DirectEditIOFile<DoubleBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, DirectEditIOFile.DOUBLE);
            }
        };
        MODEL<DirectEditIOFile<FloatBuffer>> EDIT_FLOAT_DIRECT = new MODEL<DirectEditIOFile<FloatBuffer>>() {
            @Override
            public DirectEditIOFile<FloatBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, DirectEditIOFile.FLOAT);
            }
        };
        MODEL<DirectEditIOFile<IntBuffer>> EDIT_INT_DIRECT = new MODEL<DirectEditIOFile<IntBuffer>>() {
            @Override
            public DirectEditIOFile<IntBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, DirectEditIOFile.INT);
            }
        };
        MODEL<DirectEditIOFile<CharBuffer>> EDIT_CHAR_DIRECT = new MODEL<DirectEditIOFile<CharBuffer>>() {
            @Override
            public DirectEditIOFile<CharBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, DirectEditIOFile.CHAR);
            }
        };
        MODEL<DirectEditIOFile<ByteBuffer>> EDIT_BYTE_DIRECT = new MODEL<DirectEditIOFile<ByteBuffer>>() {
            @Override
            public DirectEditIOFile<ByteBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, DirectEditIOFile.BYTE);
            }
        };
        MODEL<DirectEditIOFile<ShortBuffer>> EDIT_SHORT_DIRECT = new MODEL<DirectEditIOFile<ShortBuffer>>() {
            @Override
            public DirectEditIOFile<ShortBuffer> createIOFile(final Connector connector, final URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, DirectEditIOFile.SHORT);
            }
        };

        F createIOFile(final Connector p0, final URI p1);
    }
}
