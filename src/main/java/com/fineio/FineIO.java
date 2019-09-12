package com.fineio;

import com.fineio.exception.MemorySetException;
import com.fineio.io.file.AppendIOFile;
import com.fineio.io.file.IOFile;
import com.fineio.io.file.ReadIOFile;
import com.fineio.io.file.WriteIOFile;
import com.fineio.io.file.append.ByteAppendIOFile;
import com.fineio.io.file.append.DoubleAppendIOFile;
import com.fineio.io.file.append.IntAppendIOFile;
import com.fineio.io.file.append.LongAppendIOFile;
import com.fineio.logger.FineIOLogger;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.MemoryConf;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.storage.Connector;

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
     * @param IOFile
     * @param d
     */
    public static void put(IOFile IOFile, double d) {
        ((DoubleAppendIOFile) IOFile).put(d);
    }

    /**
     * 连续写入
     *
     * @param IOFile
     * @param d
     */
    public static void put(IOFile IOFile, byte d) {
        ((ByteAppendIOFile) IOFile).put(d);
    }


    /**
     * 连续写入
     *
     * @param IOFile
     * @param d
     */
    public static void put(IOFile IOFile, long d) {
        ((LongAppendIOFile) IOFile).put(d);
    }

    /**
     * 连续写入
     *
     * @param IOFile
     * @param d
     */
    public static void put(IOFile IOFile, int d) {
        ((IntAppendIOFile) IOFile).put(d);
    }


    /**
     * 连续写入
     *
     * @param IOFile
     * @param d
     */
    public static void put(IOFile IOFile, long pos, double d) {
        (((WriteIOFile) IOFile)).put((int) pos, d);
    }

    /**
     * 连续写入
     *
     * @param IOFile
     * @param d
     */
    public static void put(IOFile IOFile, long pos, byte d) {
        (((WriteIOFile) IOFile)).put((int) pos, d);
    }


    /**
     * 连续写入
     *
     * @param IOFile
     * @param d
     */
    public static void put(IOFile IOFile, long pos, long d) {
        (((WriteIOFile) IOFile)).put((int) pos, d);
    }

    /**
     * 连续写入
     *
     * @param IOFile
     * @param d
     */
    public static void put(IOFile IOFile, long pos, int d) {
        (((WriteIOFile) IOFile)).put((int) pos, d);
    }


    /**
     * 随机读取
     *
     * @param IOFile
     * @param p
     * @return
     */
    public final static long getLong(IOFile IOFile, long p) {
        return (((ReadIOFile) IOFile)).getLong((int) p);
    }

    /**
     * 随机读取
     *
     * @param IOFile
     * @param p
     * @return
     */
    public final static int getInt(IOFile IOFile, long p) {
        return (((ReadIOFile) IOFile)).getInt((int) p);
    }


    /**
     * 随机读取
     *
     * @param IOFile
     * @param p
     * @return
     */
    public final static double getDouble(IOFile IOFile, long p) {
        return (((ReadIOFile) IOFile)).getDouble((int) p);
    }


    /**
     * 随机读取
     *
     * @param IOFile
     * @param p
     * @return
     */
    public final static byte getByte(IOFile IOFile, long p) {
        return (((ReadIOFile) IOFile)).getByte((int) p);
    }


    public static long getFreeMemory() {
        return MemoryConf.getFreeMemory();
    }

    public interface MODEL<F> {
        FineIO.MODEL<ReadIOFile> READ_LONG = new FineIO.MODEL<ReadIOFile>() {

            @Override
            public ReadIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFile(connector, uri, MemoryConstants.OFFSET_LONG);
            }
        };
        FineIO.MODEL<ReadIOFile> READ_DOUBLE = new FineIO.MODEL<ReadIOFile>() {

            @Override
            public ReadIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFile(connector, uri, MemoryConstants.OFFSET_DOUBLE);
            }
        };


        FineIO.MODEL<ReadIOFile> READ_INT = new FineIO.MODEL<ReadIOFile>() {

            @Override
            public ReadIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFile(connector, uri, MemoryConstants.OFFSET_INT);
            }
        };
        FineIO.MODEL<ReadIOFile> READ_BYTE = new FineIO.MODEL<ReadIOFile>() {

            @Override
            public ReadIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return ReadIOFile.createFile(connector, uri, MemoryConstants.OFFSET_BYTE);
            }
        };


        FineIO.MODEL<WriteIOFile> WRITE_LONG = new FineIO.MODEL<WriteIOFile>() {

            @Override
            public WriteIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFile(connector, uri, MemoryConstants.OFFSET_LONG);
            }
        };
        FineIO.MODEL<WriteIOFile> WRITE_DOUBLE = new FineIO.MODEL<WriteIOFile>() {

            @Override
            public WriteIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFile(connector, uri, MemoryConstants.OFFSET_DOUBLE);
            }
        };
        FineIO.MODEL<WriteIOFile> WRITE_INT = new FineIO.MODEL<WriteIOFile>() {

            @Override
            public WriteIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFile(connector, uri, MemoryConstants.OFFSET_INT);
            }
        };
        FineIO.MODEL<WriteIOFile> WRITE_BYTE = new FineIO.MODEL<WriteIOFile>() {

            @Override
            public WriteIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return WriteIOFile.createFile(connector, uri, MemoryConstants.OFFSET_BYTE);
            }
        };


        FineIO.MODEL<AppendIOFile> APPEND_LONG = new FineIO.MODEL<AppendIOFile>() {

            @Override
            public AppendIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.asLong(connector, uri);
            }
        };
        FineIO.MODEL<AppendIOFile> APPEND_DOUBLE = new FineIO.MODEL<AppendIOFile>() {

            @Override
            public AppendIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.asDouble(connector, uri);
            }
        };
        FineIO.MODEL<AppendIOFile> APPEND_INT = new FineIO.MODEL<AppendIOFile>() {

            @Override
            public AppendIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.asInt(connector, uri);
            }
        };
        FineIO.MODEL<AppendIOFile> APPEND_BYTE = new FineIO.MODEL<AppendIOFile>() {

            @Override
            public AppendIOFile createIOFile(Connector connector, URI uri, boolean sync) {
                return AppendIOFile.asByte(connector, uri);
            }
        };


        F createIOFile(Connector connector, URI uri, boolean sync);
    }
}
