package com.fineio.v2;

import com.fineio.directio.DirectIOFile;
import com.fineio.exception.MemorySetException;
import com.fineio.logger.FineIOLogger;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.MemoryConf;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.storage.Connector;
import com.fineio.v2.io.ByteBuffer;
import com.fineio.v2.io.CharBuffer;
import com.fineio.v2.io.DoubleBuffer;
import com.fineio.v2.io.FloatBuffer;
import com.fineio.v2.io.IntBuffer;
import com.fineio.v2.io.LongBuffer;
import com.fineio.v2.io.ShortBuffer;
import com.fineio.v2.io.file.AppendIOFileV2;
import com.fineio.v2.io.file.FileModel;
import com.fineio.v2.io.file.IOFileV2;
import com.fineio.v2.io.file.ReadIOFileV2;
import com.fineio.v2.io.file.WriteIOFileV2;

import java.net.URI;

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
        return createIOFile(connector, uri, model, true);
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
    public static void put(IOFileV2<DoubleBuffer> file, double d) {
        IOFileV2.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<ByteBuffer> file, byte d) {
        IOFileV2.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<CharBuffer> file, char d) {
        IOFileV2.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<FloatBuffer> file, float d) {
        IOFileV2.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<LongBuffer> file, long d) {
        IOFileV2.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<IntBuffer> file, int d) {
        IOFileV2.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<ShortBuffer> file, short d) {
        IOFileV2.put(file, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<DoubleBuffer> file, long pos, double d) {
        IOFileV2.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<ByteBuffer> file, long pos, byte d) {
        IOFileV2.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<CharBuffer> file, long pos, char d) {
        IOFileV2.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<FloatBuffer> file, long pos, float d) {
        IOFileV2.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<LongBuffer> file, long pos, long d) {
        IOFileV2.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<IntBuffer> file, long pos, int d) {
        IOFileV2.put(file, pos, d);
    }

    /**
     * 连续写入
     *
     * @param file
     * @param d
     */
    public static void put(IOFileV2<ShortBuffer> file, long pos, short d) {
        IOFileV2.put(file, pos, d);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static long getLong(IOFileV2<LongBuffer> file, long p) {
        return IOFileV2.getLong(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static int getInt(IOFileV2<IntBuffer> file, long p) {
        return IOFileV2.getInt(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static char getChar(IOFileV2<CharBuffer> file, long p) {
        return IOFileV2.getChar(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static double getDouble(IOFileV2<DoubleBuffer> file, long p) {
        return IOFileV2.getDouble(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static float getFloat(IOFileV2<FloatBuffer> file, long p) {
        return IOFileV2.getFloat(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static byte getByte(IOFileV2<ByteBuffer> file, long p) {
        return IOFileV2.getByte(file, p);
    }

    /**
     * 随机读取
     *
     * @param file
     * @param p
     * @return
     */
    public final static short getShort(IOFileV2<ShortBuffer> file, long p) {
        return IOFileV2.getShort(file, p);
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

    public static long getFreeMemory() {
        return MemoryConf.getFreeMemory();
    }

    public interface MODEL<F> {
        FineIO.MODEL<ReadIOFileV2<LongBuffer>> READ_LONG = new FineIO.MODEL<ReadIOFileV2<LongBuffer>>() {

            @Override
            public ReadIOFileV2<LongBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFileV2.createFineIO(connector, uri, FileModel.LONG);
            }
        };
        FineIO.MODEL<ReadIOFileV2<DoubleBuffer>> READ_DOUBLE = new FineIO.MODEL<ReadIOFileV2<DoubleBuffer>>() {

            @Override
            public ReadIOFileV2<DoubleBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFileV2.createFineIO(connector, uri, FileModel.DOUBLE);
            }
        };

        FineIO.MODEL<ReadIOFileV2<FloatBuffer>> READ_FLOAT = new FineIO.MODEL<ReadIOFileV2<FloatBuffer>>() {

            @Override
            public ReadIOFileV2<FloatBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFileV2.createFineIO(connector, uri, FileModel.FLOAT);
            }
        };

        FineIO.MODEL<ReadIOFileV2<IntBuffer>> READ_INT = new FineIO.MODEL<ReadIOFileV2<IntBuffer>>() {

            @Override
            public ReadIOFileV2<IntBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFileV2.createFineIO(connector, uri, FileModel.INT);
            }
        };
        FineIO.MODEL<ReadIOFileV2<CharBuffer>> READ_CHAR = new FineIO.MODEL<ReadIOFileV2<CharBuffer>>() {

            @Override
            public ReadIOFileV2<CharBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFileV2.createFineIO(connector, uri, FileModel.CHAR);
            }
        };
        FineIO.MODEL<ReadIOFileV2<ByteBuffer>> READ_BYTE = new FineIO.MODEL<ReadIOFileV2<ByteBuffer>>() {

            @Override
            public ReadIOFileV2<ByteBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFileV2.createFineIO(connector, uri, FileModel.BYTE);
            }
        };

        FineIO.MODEL<ReadIOFileV2<ShortBuffer>> READ_SHORT = new FineIO.MODEL<ReadIOFileV2<ShortBuffer>>() {

            @Override
            public ReadIOFileV2<ShortBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFileV2.createFineIO(connector, uri, FileModel.SHORT);
            }
        };


        FineIO.MODEL<WriteIOFileV2<LongBuffer>> WRITE_LONG = new FineIO.MODEL<WriteIOFileV2<LongBuffer>>() {

            @Override
            public WriteIOFileV2<LongBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFileV2.createFineIO(connector, uri, FileModel.LONG, sync);
            }
        };
        FineIO.MODEL<WriteIOFileV2<DoubleBuffer>> WRITE_DOUBLE = new FineIO.MODEL<WriteIOFileV2<DoubleBuffer>>() {

            @Override
            public WriteIOFileV2<DoubleBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFileV2.createFineIO(connector, uri, FileModel.DOUBLE, sync);
            }
        };
        FineIO.MODEL<WriteIOFileV2<FloatBuffer>> WRITE_FLOAT = new FineIO.MODEL<WriteIOFileV2<FloatBuffer>>() {

            @Override
            public WriteIOFileV2<FloatBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFileV2.createFineIO(connector, uri, FileModel.FLOAT, sync);
            }
        };
        FineIO.MODEL<WriteIOFileV2<IntBuffer>> WRITE_INT = new FineIO.MODEL<WriteIOFileV2<IntBuffer>>() {

            @Override
            public WriteIOFileV2<IntBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFileV2.createFineIO(connector, uri, FileModel.INT, sync);
            }
        };
        FineIO.MODEL<WriteIOFileV2<CharBuffer>> WRITE_CHAR = new FineIO.MODEL<WriteIOFileV2<CharBuffer>>() {

            @Override
            public WriteIOFileV2<CharBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFileV2.createFineIO(connector, uri, FileModel.CHAR, sync);
            }
        };
        FineIO.MODEL<WriteIOFileV2<ByteBuffer>> WRITE_BYTE = new FineIO.MODEL<WriteIOFileV2<ByteBuffer>>() {

            @Override
            public WriteIOFileV2<ByteBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFileV2.createFineIO(connector, uri, FileModel.BYTE, sync);
            }
        };

        FineIO.MODEL<WriteIOFileV2<ShortBuffer>> WRITE_SHORT = new FineIO.MODEL<WriteIOFileV2<ShortBuffer>>() {

            @Override
            public WriteIOFileV2<ShortBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFileV2.createFineIO(connector, uri, FileModel.SHORT, sync);
            }
        };

        FineIO.MODEL<AppendIOFileV2<LongBuffer>> APPEND_LONG = new FineIO.MODEL<AppendIOFileV2<LongBuffer>>() {

            @Override
            public AppendIOFileV2<LongBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFileV2.createFineIO(connector, uri, FileModel.LONG, sync);
            }
        };
        FineIO.MODEL<AppendIOFileV2<DoubleBuffer>> APPEND_DOUBLE = new FineIO.MODEL<AppendIOFileV2<DoubleBuffer>>() {

            @Override
            public AppendIOFileV2<DoubleBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFileV2.createFineIO(connector, uri, FileModel.DOUBLE, sync);
            }
        };
        FineIO.MODEL<AppendIOFileV2<FloatBuffer>> APPEND_FLOAT = new FineIO.MODEL<AppendIOFileV2<FloatBuffer>>() {

            @Override
            public AppendIOFileV2<FloatBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFileV2.createFineIO(connector, uri, FileModel.FLOAT, sync);
            }
        };
        FineIO.MODEL<AppendIOFileV2<IntBuffer>> APPEND_INT = new FineIO.MODEL<AppendIOFileV2<IntBuffer>>() {

            @Override
            public AppendIOFileV2<IntBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFileV2.createFineIO(connector, uri, FileModel.INT, sync);
            }
        };
        FineIO.MODEL<AppendIOFileV2<CharBuffer>> APPEND_CHAR = new FineIO.MODEL<AppendIOFileV2<CharBuffer>>() {

            @Override
            public AppendIOFileV2<CharBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFileV2.createFineIO(connector, uri, FileModel.CHAR, sync);
            }
        };
        FineIO.MODEL<AppendIOFileV2<ByteBuffer>> APPEND_BYTE = new FineIO.MODEL<AppendIOFileV2<ByteBuffer>>() {

            @Override
            public AppendIOFileV2<ByteBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFileV2.createFineIO(connector, uri, FileModel.BYTE, sync);
            }
        };

        FineIO.MODEL<AppendIOFileV2<ShortBuffer>> APPEND_SHORT = new FineIO.MODEL<AppendIOFileV2<ShortBuffer>>() {

            @Override
            public AppendIOFileV2<ShortBuffer> createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFileV2.createFineIO(connector, uri, FileModel.SHORT, sync);
            }
        };

        F createIOFile(Connector connector, URI uri, boolean sync);
    }
}
