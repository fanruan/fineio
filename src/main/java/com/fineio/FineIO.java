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
import com.fineio.io.FileModel;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.EditIOFile;
import com.fineio.io.file.IOFile;
import com.fineio.io.file.ReadIOFile;
import com.fineio.io.file.WriteIOFile;
import com.fineio.io.file.writer.SyncManager;
import com.fineio.logger.FineIOLogger;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.MemoryConf;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/6/1
 */
public final class FineIO {
    /**
     * 创建IO文件
     *
     * @param connector 连接器
     * @param uri       uri
     * @param model     模式
     * @return
     */
    public static <F> F createIOFile(Connector connector, URI uri, MODEL<F> model) {
        return model.createIOFile(connector, uri);
    }

    public static void setLogger(FineIOLogger logger) {
        FineIOLoggers.setLogger(logger);
    }

    /**
     * @return
     * @see MemoryConf
     */
    public static long getTotalMemSize() {
        return MemoryConf.getTotalMemSize();
    }

    /**
     * @param size
     * @throws MemorySetException
     * @see MemoryConf
     */
    public static void setTotalMemSize(long size) throws MemorySetException {
        MemoryConf.setTotalMemSize(size);
    }

    /**
     * @return
     * @see MemoryConf
     */
    public final static long getMaxMemSizeForSet() {
        return MemoryConf.getMaxMemSizeForSet();
    }

    /**
     * @return
     * @see MemoryConf
     */
    public final static long getMinMemSizeForSet() {
        return MemoryConf.getMinMemSizeForSet();
    }

    /**
     * 设置内存超时检查时间
     *
     * @param t
     * @return
     */
    public final static void setMemoryCheckSchedule(long t) {
        CacheManager.getInstance().resetTimer(t);
    }

    /**
     * 获取写线程数量
     *
     * @return
     */
    public static int getSyncThreads() {
        return SyncManager.getInstance().getThreads();
    }

    /**
     * 设置写线程数量
     *
     * @param threads
     */
    public static void setSyncThreads(int threads) {
        SyncManager.getInstance().setThreads(threads);
    }

    /**
     * 获取总的可用空闲内存大小
     *
     * @return
     */
    public static long getFreeMemory() {
        return MemoryConf.getFreeMemory();
    }

    /**
     * 获取当前内存使用量
     */
    public static long getCurrentMemorySize() {
        return CacheManager.getInstance().getCurrentMemorySize();
    }

    /**
     * 获取当前读内存使用量
     */
    public static long getCurrentReadMemorySize() {
        return CacheManager.getInstance().getReadSize();
    }

    /**
     * 获取当前写内存使用量
     */
    public static long getCurrentWriteMemorySize() {
        return CacheManager.getInstance().getWriteSize();
    }

    /**
     * 获取当前等待读的句柄数
     */
    public static long getReadWaitCount() {
        return CacheManager.getInstance().getReadWaitCount();
    }

    /**
     * 获取当前写文件等待读的句柄数
     */
    public static long getWriteWaitCount() {
        return CacheManager.getInstance().getWriteWaitCount();
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<DoubleBuffer> file, long p, double d) {
        IOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<ByteBuffer> file, long p, byte d) {
        IOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<CharBuffer> file, long p, char d) {
        IOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<FloatBuffer> file, long p, float d) {
        IOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<LongBuffer> file, long p, long d) {
        IOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<IntBuffer> file, long p, int d) {
        IOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<ShortBuffer> file, long p, short d) {
        IOFile.put(file, p, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<DoubleBuffer> file, double d) {
        IOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<ByteBuffer> file, byte d) {
        IOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<CharBuffer> file, char d) {
        IOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<FloatBuffer> file, float d) {
        IOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<LongBuffer> file, long d) {
        IOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<IntBuffer> file, int d) {
        IOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<ShortBuffer> file, short d) {
        IOFile.put(file, d);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static long getLong(IOFile<LongBuffer> file, long p) {
        return IOFile.getLong(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static int getInt(IOFile<IntBuffer> file, long p) {
        return IOFile.getInt(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static char getChar(IOFile<CharBuffer> file, long p) {
        return IOFile.getChar(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static double getDouble(IOFile<DoubleBuffer> file, long p) {
        return IOFile.getDouble(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static float getFloat(IOFile<FloatBuffer> file, long p) {
        return IOFile.getFloat(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static byte getByte(IOFile<ByteBuffer> file, long p) {
        return IOFile.getByte(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static short getShort(IOFile<ShortBuffer> file, long p) {
        return IOFile.getShort(file, p);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<DoubleBuffer> file, int p, double d) {
        DirectIOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<ByteBuffer> file, int p, byte d) {
        DirectIOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<CharBuffer> file, int p, char d) {
        DirectIOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<FloatBuffer> file, int p, float d) {
        DirectIOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<LongBuffer> file, int p, long d) {
        DirectIOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<IntBuffer> file, int p, int d) {
        DirectIOFile.put(file, p, d);
    }

    /**
     * 随机写入
     *
     * @param file
     * @param p
     * @param d
     */
    public static void put(DirectIOFile<ShortBuffer> file, int p, short d) {
        DirectIOFile.put(file, p, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(DirectIOFile<DoubleBuffer> file, double d) {
        DirectIOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(DirectIOFile<ByteBuffer> file, byte d) {
        DirectIOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(DirectIOFile<CharBuffer> file, char d) {
        DirectIOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(DirectIOFile<FloatBuffer> file, float d) {
        DirectIOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(DirectIOFile<LongBuffer> file, long d) {
        DirectIOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(DirectIOFile<IntBuffer> file, int d) {
        DirectIOFile.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(DirectIOFile<ShortBuffer> file, short d) {
        DirectIOFile.put(file, d);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static long getLong(DirectIOFile<LongBuffer> file, int p) {
        return DirectIOFile.getLong(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static int getInt(DirectIOFile<IntBuffer> file, int p) {
        return DirectIOFile.getInt(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static char getChar(DirectIOFile<CharBuffer> file, int p) {
        return DirectIOFile.getChar(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static double getDouble(DirectIOFile<DoubleBuffer> file, int p) {
        return DirectIOFile.getDouble(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static float getFloat(DirectIOFile<FloatBuffer> file, int p) {
        return DirectIOFile.getFloat(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static byte getByte(DirectIOFile<ByteBuffer> file, int p) {
        return DirectIOFile.getByte(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static short getShort(DirectIOFile<ShortBuffer> file, int p) {
        return DirectIOFile.getShort(file, p);
    }

    public interface MODEL<F> {
        MODEL<ReadIOFile<LongBuffer>> READ_LONG = new MODEL<ReadIOFile<LongBuffer>>() {

            public ReadIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.LONG);
            }
        };
        MODEL<ReadIOFile<DoubleBuffer>> READ_DOUBLE = new MODEL<ReadIOFile<DoubleBuffer>>() {

            public ReadIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.DOUBLE);
            }
        };

        MODEL<ReadIOFile<FloatBuffer>> READ_FLOAT = new MODEL<ReadIOFile<FloatBuffer>>() {

            public ReadIOFile<FloatBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.FLOAT);
            }
        };

        MODEL<ReadIOFile<IntBuffer>> READ_INT = new MODEL<ReadIOFile<IntBuffer>>() {

            public ReadIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.INT);
            }
        };
        MODEL<ReadIOFile<CharBuffer>> READ_CHAR = new MODEL<ReadIOFile<CharBuffer>>() {

            public ReadIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.CHAR);
            }
        };
        MODEL<ReadIOFile<ByteBuffer>> READ_BYTE = new MODEL<ReadIOFile<ByteBuffer>>() {

            public ReadIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.BYTE);
            }
        };

        MODEL<ReadIOFile<ShortBuffer>> READ_SHORT = new MODEL<ReadIOFile<ShortBuffer>>() {

            public ReadIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.SHORT);
            }
        };


        MODEL<WriteIOFile<LongBuffer>> WRITE_LONG = new MODEL<WriteIOFile<LongBuffer>>() {

            public WriteIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.LONG);
            }
        };
        MODEL<WriteIOFile<DoubleBuffer>> WRITE_DOUBLE = new MODEL<WriteIOFile<DoubleBuffer>>() {

            public WriteIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.DOUBLE);
            }
        };
        MODEL<WriteIOFile<FloatBuffer>> WRITE_FLOAT = new MODEL<WriteIOFile<FloatBuffer>>() {

            public WriteIOFile<FloatBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.FLOAT);
            }
        };
        MODEL<WriteIOFile<IntBuffer>> WRITE_INT = new MODEL<WriteIOFile<IntBuffer>>() {

            public WriteIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.INT);
            }
        };
        MODEL<WriteIOFile<CharBuffer>> WRITE_CHAR = new MODEL<WriteIOFile<CharBuffer>>() {

            public WriteIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.CHAR);
            }
        };
        MODEL<WriteIOFile<ByteBuffer>> WRITE_BYTE = new MODEL<WriteIOFile<ByteBuffer>>() {

            public WriteIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.BYTE);
            }
        };

        MODEL<WriteIOFile<ShortBuffer>> WRITE_SHORT = new MODEL<WriteIOFile<ShortBuffer>>() {

            public WriteIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.SHORT);
            }
        };

        MODEL<EditIOFile<LongBuffer>> EDIT_LONG = new MODEL<EditIOFile<LongBuffer>>() {

            public EditIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, FileModel.LONG);
            }
        };
        MODEL<EditIOFile<DoubleBuffer>> EDIT_DOUBLE = new MODEL<EditIOFile<DoubleBuffer>>() {

            public EditIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, FileModel.DOUBLE);
            }
        };
        MODEL<EditIOFile<FloatBuffer>> EDIT_FLOAT = new MODEL<EditIOFile<FloatBuffer>>() {

            public EditIOFile<FloatBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, FileModel.FLOAT);
            }
        };
        MODEL<EditIOFile<IntBuffer>> EDIT_INT = new MODEL<EditIOFile<IntBuffer>>() {

            public EditIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, FileModel.INT);
            }
        };
        MODEL<EditIOFile<CharBuffer>> EDIT_CHAR = new MODEL<EditIOFile<CharBuffer>>() {

            public EditIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, FileModel.CHAR);
            }
        };
        MODEL<EditIOFile<ByteBuffer>> EDIT_BYTE = new MODEL<EditIOFile<ByteBuffer>>() {

            public EditIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, FileModel.BYTE);
            }
        };

        MODEL<EditIOFile<ShortBuffer>> EDIT_SHORT = new MODEL<EditIOFile<ShortBuffer>>() {

            public EditIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, FileModel.SHORT);
            }
        };


        MODEL<DirectReadIOFile<LongBuffer>> READ_LONG_DIRECT = new MODEL<DirectReadIOFile<LongBuffer>>() {

            public DirectReadIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.LONG);
            }
        };
        MODEL<DirectReadIOFile<DoubleBuffer>> READ_DOUBLE_DIRECT = new MODEL<DirectReadIOFile<DoubleBuffer>>() {

            public DirectReadIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.DOUBLE);
            }
        };

        MODEL<DirectReadIOFile<FloatBuffer>> READ_FLOAT_DIRECT = new MODEL<DirectReadIOFile<FloatBuffer>>() {

            public DirectReadIOFile<FloatBuffer> createIOFile(Connector connector, URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.FLOAT);
            }
        };

        MODEL<DirectReadIOFile<IntBuffer>> READ_INT_DIRECT = new MODEL<DirectReadIOFile<IntBuffer>>() {

            public DirectReadIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.INT);
            }
        };
        MODEL<DirectReadIOFile<CharBuffer>> READ_CHAR_DIRECT = new MODEL<DirectReadIOFile<CharBuffer>>() {

            public DirectReadIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.CHAR);
            }
        };
        MODEL<DirectReadIOFile<ByteBuffer>> READ_BYTE_DIRECT = new MODEL<DirectReadIOFile<ByteBuffer>>() {

            public DirectReadIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.BYTE);
            }
        };

        MODEL<DirectReadIOFile<ShortBuffer>> READ_SHORT_DIRECT = new MODEL<DirectReadIOFile<ShortBuffer>>() {

            public DirectReadIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.SHORT);
            }
        };


        MODEL<DirectWriteIOFile<LongBuffer>> WRITE_LONG_DIRECT = new MODEL<DirectWriteIOFile<LongBuffer>>() {

            public DirectWriteIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.LONG);
            }
        };
        MODEL<DirectWriteIOFile<DoubleBuffer>> WRITE_DOUBLE_DIRECT = new MODEL<DirectWriteIOFile<DoubleBuffer>>() {

            public DirectWriteIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.DOUBLE);
            }
        };
        MODEL<DirectWriteIOFile<FloatBuffer>> WRITE_FLOAT_DIRECT = new MODEL<DirectWriteIOFile<FloatBuffer>>() {

            public DirectWriteIOFile<FloatBuffer> createIOFile(Connector connector, URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.FLOAT);
            }
        };
        MODEL<DirectWriteIOFile<IntBuffer>> WRITE_INT_DIRECT = new MODEL<DirectWriteIOFile<IntBuffer>>() {

            public DirectWriteIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.INT);
            }
        };
        MODEL<DirectWriteIOFile<CharBuffer>> WRITE_CHAR_DIRECT = new MODEL<DirectWriteIOFile<CharBuffer>>() {

            public DirectWriteIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.CHAR);
            }
        };
        MODEL<DirectWriteIOFile<ByteBuffer>> WRITE_BYTE_DIRECT = new MODEL<DirectWriteIOFile<ByteBuffer>>() {

            public DirectWriteIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.BYTE);
            }
        };

        MODEL<DirectWriteIOFile<ShortBuffer>> WRITE_SHORT_DIRECT = new MODEL<DirectWriteIOFile<ShortBuffer>>() {

            public DirectWriteIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.SHORT);
            }
        };

        MODEL<DirectEditIOFile<LongBuffer>> EDIT_LONG_DIRECT = new MODEL<DirectEditIOFile<LongBuffer>>() {

            public DirectEditIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, FileModel.LONG);
            }
        };
        MODEL<DirectEditIOFile<DoubleBuffer>> EDIT_DOUBLE_DIRECT = new MODEL<DirectEditIOFile<DoubleBuffer>>() {

            public DirectEditIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, FileModel.DOUBLE);
            }
        };
        MODEL<DirectEditIOFile<FloatBuffer>> EDIT_FLOAT_DIRECT = new MODEL<DirectEditIOFile<FloatBuffer>>() {

            public DirectEditIOFile<FloatBuffer> createIOFile(Connector connector, URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, FileModel.FLOAT);
            }
        };
        MODEL<DirectEditIOFile<IntBuffer>> EDIT_INT_DIRECT = new MODEL<DirectEditIOFile<IntBuffer>>() {

            public DirectEditIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, FileModel.INT);
            }
        };
        MODEL<DirectEditIOFile<CharBuffer>> EDIT_CHAR_DIRECT = new MODEL<DirectEditIOFile<CharBuffer>>() {

            public DirectEditIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, FileModel.CHAR);
            }
        };
        MODEL<DirectEditIOFile<ByteBuffer>> EDIT_BYTE_DIRECT = new MODEL<DirectEditIOFile<ByteBuffer>>() {

            public DirectEditIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, FileModel.BYTE);
            }
        };

        MODEL<DirectEditIOFile<ShortBuffer>> EDIT_SHORT_DIRECT = new MODEL<DirectEditIOFile<ShortBuffer>>() {

            public DirectEditIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return DirectEditIOFile.createFineIO(connector, uri, FileModel.SHORT);
            }
        };

        F createIOFile(Connector connector, URI uri);
    }
}
