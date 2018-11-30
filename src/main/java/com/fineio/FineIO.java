package com.fineio;

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
import com.fineio.io.file.AppendIOFile;
import com.fineio.io.file.FileModel;
import com.fineio.io.file.IOFile;
import com.fineio.io.file.ReadIOFile;
import com.fineio.io.file.WriteIOFile;
import com.fineio.io.file.writer.JobFinishedManager;
import com.fineio.logger.FineIOLogger;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.MemoryConf;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.storage.Connector;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class FineIO {

    public static boolean DEBUG = false;

    /**
     * 创建IO文件
     *
     * @param connector 连接器
     * @param uri       uri
     * @param model     模式
     * @return
     */
    public static <F> F createIOFile(Connector connector, URI uri, FineIO.MODEL<F> model) {
        return model.createIOFile(connector, uri, false);
    }

    public static <F> F createIOFile(Connector connector, URI uri, FineIO.MODEL<F> model, boolean sync) {
        return model.createIOFile(connector, uri, sync);
    }

    /**
     * 设置Logger
     *
     * @param logger
     */
    public static void setLogger(FineIOLogger logger, boolean debug) {
        DEBUG = debug;
        FineIOLoggers.setLogger(logger);
    }

    /**
     * 设置Logger
     *
     * @param logger
     */
    public static void setLogger(FineIOLogger logger) {
        setLogger(logger, false);
    }

    /**
     * 前面的任务都结束了调用
     *
     * @param runnable
     */
    public static Future<Void> doWhenFinished(Runnable runnable) {
        return JobFinishedManager.getInstance().finish(runnable);
    }

    /**
     * 前面的任务都结束了调用
     *
     * @param callable
     */
    public static <T> Future<T> doWhenFinished(Callable<T> callable) {
        return JobFinishedManager.getInstance().finish(callable);
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
     * 获取当前内存使用量
     */
    public static long getCurrentMemorySize() {
        return MemoryManager.INSTANCE.getCurrentMemorySize();
    }

    /**
     * 获取当前读内存使用量
     */
    public static long getCurrentReadMemorySize() {
        return MemoryManager.INSTANCE.getReadSize();
    }

    /**
     * 获取当前写内存使用量
     */
    public static long getCurrentWriteMemorySize() {
        return MemoryManager.INSTANCE.getWriteSize();
    }

    /**
     * 获取当前等待读的句柄数
     */
    public static long getReadWaitCount() {
        return MemoryManager.INSTANCE.getReadWaitCount();
    }

    /**
     * 获取当前写文件等待读的句柄数
     */
    public static long getWriteWaitCount() {
        return MemoryManager.INSTANCE.getWriteWaitCount();
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
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<DoubleBuffer> file, long pos, double d) {
        IOFile.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<ByteBuffer> file, long pos, byte d) {
        IOFile.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<CharBuffer> file, long pos, char d) {
        IOFile.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<FloatBuffer> file, long pos, float d) {
        IOFile.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<LongBuffer> file, long pos, long d) {
        IOFile.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<IntBuffer> file, long pos, int d) {
        IOFile.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFile<ShortBuffer> file, long pos, short d) {
        IOFile.put(file, pos, d);
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
        FineIO.MODEL<ReadIOFile<LongBuffer>> READ_LONG = new FineIO.MODEL<ReadIOFile<LongBuffer>>() {

            @Override
            public ReadIOFile<LongBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.LONG);
            }
        };
        FineIO.MODEL<ReadIOFile<DoubleBuffer>> READ_DOUBLE = new FineIO.MODEL<ReadIOFile<DoubleBuffer>>() {

            @Override
            public ReadIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.DOUBLE);
            }
        };

        FineIO.MODEL<ReadIOFile<FloatBuffer>> READ_FLOAT = new FineIO.MODEL<ReadIOFile<FloatBuffer>>() {

            @Override
            public ReadIOFile<FloatBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.FLOAT);
            }
        };

        FineIO.MODEL<ReadIOFile<IntBuffer>> READ_INT = new FineIO.MODEL<ReadIOFile<IntBuffer>>() {

            @Override
            public ReadIOFile<IntBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.INT);
            }
        };
        FineIO.MODEL<ReadIOFile<CharBuffer>> READ_CHAR = new FineIO.MODEL<ReadIOFile<CharBuffer>>() {

            @Override
            public ReadIOFile<CharBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.CHAR);
            }
        };
        FineIO.MODEL<ReadIOFile<ByteBuffer>> READ_BYTE = new FineIO.MODEL<ReadIOFile<ByteBuffer>>() {

            @Override
            public ReadIOFile<ByteBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.BYTE);
            }
        };

        FineIO.MODEL<ReadIOFile<ShortBuffer>> READ_SHORT = new FineIO.MODEL<ReadIOFile<ShortBuffer>>() {

            @Override
            public ReadIOFile<ShortBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFineIO(connector, uri, FileModel.SHORT);
            }
        };


        FineIO.MODEL<WriteIOFile<LongBuffer>> WRITE_LONG = new FineIO.MODEL<WriteIOFile<LongBuffer>>() {

            @Override
            public WriteIOFile<LongBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.LONG, sync);
            }
        };
        FineIO.MODEL<WriteIOFile<DoubleBuffer>> WRITE_DOUBLE = new FineIO.MODEL<WriteIOFile<DoubleBuffer>>() {

            @Override
            public WriteIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.DOUBLE, sync);
            }
        };
        FineIO.MODEL<WriteIOFile<FloatBuffer>> WRITE_FLOAT = new FineIO.MODEL<WriteIOFile<FloatBuffer>>() {

            @Override
            public WriteIOFile<FloatBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.FLOAT, sync);
            }
        };
        FineIO.MODEL<WriteIOFile<IntBuffer>> WRITE_INT = new FineIO.MODEL<WriteIOFile<IntBuffer>>() {

            @Override
            public WriteIOFile<IntBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.INT, sync);
            }
        };
        FineIO.MODEL<WriteIOFile<CharBuffer>> WRITE_CHAR = new FineIO.MODEL<WriteIOFile<CharBuffer>>() {

            @Override
            public WriteIOFile<CharBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.CHAR, sync);
            }
        };
        FineIO.MODEL<WriteIOFile<ByteBuffer>> WRITE_BYTE = new FineIO.MODEL<WriteIOFile<ByteBuffer>>() {

            @Override
            public WriteIOFile<ByteBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.BYTE, sync);
            }
        };

        FineIO.MODEL<WriteIOFile<ShortBuffer>> WRITE_SHORT = new FineIO.MODEL<WriteIOFile<ShortBuffer>>() {

            @Override
            public WriteIOFile<ShortBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFineIO(connector, uri, FileModel.SHORT, sync);
            }
        };

        FineIO.MODEL<AppendIOFile<LongBuffer>> APPEND_LONG = new FineIO.MODEL<AppendIOFile<LongBuffer>>() {

            @Override
            public AppendIOFile<LongBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.createFineIO(connector, uri, FileModel.LONG, sync);
            }
        };
        FineIO.MODEL<AppendIOFile<DoubleBuffer>> APPEND_DOUBLE = new FineIO.MODEL<AppendIOFile<DoubleBuffer>>() {

            @Override
            public AppendIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.createFineIO(connector, uri, FileModel.DOUBLE, sync);
            }
        };
        FineIO.MODEL<AppendIOFile<FloatBuffer>> APPEND_FLOAT = new FineIO.MODEL<AppendIOFile<FloatBuffer>>() {

            @Override
            public AppendIOFile<FloatBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.createFineIO(connector, uri, FileModel.FLOAT, sync);
            }
        };
        FineIO.MODEL<AppendIOFile<IntBuffer>> APPEND_INT = new FineIO.MODEL<AppendIOFile<IntBuffer>>() {

            @Override
            public AppendIOFile<IntBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.createFineIO(connector, uri, FileModel.INT, sync);
            }
        };
        FineIO.MODEL<AppendIOFile<CharBuffer>> APPEND_CHAR = new FineIO.MODEL<AppendIOFile<CharBuffer>>() {

            @Override
            public AppendIOFile<CharBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.createFineIO(connector, uri, FileModel.CHAR, sync);
            }
        };
        FineIO.MODEL<AppendIOFile<ByteBuffer>> APPEND_BYTE = new FineIO.MODEL<AppendIOFile<ByteBuffer>>() {

            @Override
            public AppendIOFile<ByteBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.createFineIO(connector, uri, FileModel.BYTE, sync);
            }
        };

        FineIO.MODEL<AppendIOFile<ShortBuffer>> APPEND_SHORT = new FineIO.MODEL<AppendIOFile<ShortBuffer>>() {

            @Override
            public AppendIOFile<ShortBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.createFineIO(connector, uri, FileModel.SHORT, sync);
            }
        };

        FineIO.MODEL<DirectReadIOFile<LongBuffer>> DIRECT_READ_LONG = new FineIO.MODEL<DirectReadIOFile<LongBuffer>>() {

            @Override
            public DirectReadIOFile<LongBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.LONG);
            }
        };
        FineIO.MODEL<DirectReadIOFile<DoubleBuffer>> DIRECT_READ_DOUBLE = new FineIO.MODEL<DirectReadIOFile<DoubleBuffer>>() {

            @Override
            public DirectReadIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.DOUBLE);
            }
        };

        FineIO.MODEL<DirectReadIOFile<FloatBuffer>> DIRECT_READ_FLOAT = new FineIO.MODEL<DirectReadIOFile<FloatBuffer>>() {

            @Override
            public DirectReadIOFile<FloatBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.FLOAT);
            }
        };

        FineIO.MODEL<DirectReadIOFile<IntBuffer>> DIRECT_READ_INT = new FineIO.MODEL<DirectReadIOFile<IntBuffer>>() {

            @Override
            public DirectReadIOFile<IntBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.INT);
            }
        };
        FineIO.MODEL<DirectReadIOFile<CharBuffer>> DIRECT_READ_CHAR = new FineIO.MODEL<DirectReadIOFile<CharBuffer>>() {

            @Override
            public DirectReadIOFile<CharBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.CHAR);
            }
        };
        FineIO.MODEL<DirectReadIOFile<ByteBuffer>> DIRECT_READ_BYTE = new FineIO.MODEL<DirectReadIOFile<ByteBuffer>>() {

            @Override
            public DirectReadIOFile<ByteBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.BYTE);
            }
        };

        FineIO.MODEL<DirectReadIOFile<ShortBuffer>> DIRECT_READ_SHORT = new FineIO.MODEL<DirectReadIOFile<ShortBuffer>>() {

            @Override
            public DirectReadIOFile<ShortBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectReadIOFile.createFineIO(connector, uri, FileModel.SHORT);
            }
        };


        FineIO.MODEL<DirectWriteIOFile<LongBuffer>> DIRECT_WRITE_LONG = new FineIO.MODEL<DirectWriteIOFile<LongBuffer>>() {

            @Override
            public DirectWriteIOFile<LongBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.LONG, sync);
            }
        };
        FineIO.MODEL<DirectWriteIOFile<DoubleBuffer>> DIRECT_WRITE_DOUBLE = new FineIO.MODEL<DirectWriteIOFile<DoubleBuffer>>() {

            @Override
            public DirectWriteIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.DOUBLE, sync);
            }
        };
        FineIO.MODEL<DirectWriteIOFile<FloatBuffer>> DIRECT_WRITE_FLOAT = new FineIO.MODEL<DirectWriteIOFile<FloatBuffer>>() {

            @Override
            public DirectWriteIOFile<FloatBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.FLOAT, sync);
            }
        };
        FineIO.MODEL<DirectWriteIOFile<IntBuffer>> DIRECT_WRITE_INT = new FineIO.MODEL<DirectWriteIOFile<IntBuffer>>() {

            @Override
            public DirectWriteIOFile<IntBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.INT, sync);
            }
        };
        FineIO.MODEL<DirectWriteIOFile<CharBuffer>> DIRECT_WRITE_CHAR = new FineIO.MODEL<DirectWriteIOFile<CharBuffer>>() {

            @Override
            public DirectWriteIOFile<CharBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.CHAR, sync);
            }
        };
        FineIO.MODEL<DirectWriteIOFile<ByteBuffer>> DIRECT_WRITE_BYTE = new FineIO.MODEL<DirectWriteIOFile<ByteBuffer>>() {

            @Override
            public DirectWriteIOFile<ByteBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.BYTE, sync);
            }
        };

        FineIO.MODEL<DirectWriteIOFile<ShortBuffer>> DIRECT_WRITE_SHORT = new FineIO.MODEL<DirectWriteIOFile<ShortBuffer>>() {

            @Override
            public DirectWriteIOFile<ShortBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return DirectWriteIOFile.createFineIO(connector, uri, FileModel.SHORT, sync);
            }
        };

        F createIOFile(Connector connector, URI uri, boolean sync);
    }
}
