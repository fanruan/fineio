package com.fineio;


import com.fineio.cache.CacheManager;
import com.fineio.exception.MemorySetException;
import com.fineio.io.file.EditIOFile;
import com.fineio.io.file.IOFile;
import com.fineio.io.file.ReadIOFile;
import com.fineio.io.file.WriteIOFile;
import com.fineio.io.*;
import com.fineio.io.file.writer.SyncManager;
import com.fineio.memory.MemoryConf;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final  class FineIO {

    public interface MODEL<F> {
         MODEL<ReadIOFile<LongBuffer>> READ_LONG = new MODEL<ReadIOFile<LongBuffer>>() {

             public ReadIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                 return ReadIOFile.createFineIO(connector, uri, ReadIOFile.LONG);
             }
         };
        MODEL<ReadIOFile<DoubleBuffer>> READ_DOUBLE = new MODEL<ReadIOFile<DoubleBuffer>>() {

            public ReadIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.DOUBLE);
            }
        };

        MODEL<ReadIOFile<FloatBuffer>> READ_FLOAT = new MODEL<ReadIOFile<FloatBuffer>>() {

            public ReadIOFile<FloatBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.FLOAT);
            }
        };

        MODEL<ReadIOFile<IntBuffer>> READ_INT = new MODEL<ReadIOFile<IntBuffer>>() {

            public ReadIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.INT);
            }
        };
        MODEL<ReadIOFile<CharBuffer> > READ_CHAR = new MODEL<ReadIOFile<CharBuffer> >() {

            public ReadIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.CHAR);
            }
        };
        MODEL<ReadIOFile<ByteBuffer> > READ_BYTE = new MODEL<ReadIOFile<ByteBuffer> >() {

            public ReadIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.BYTE);
            }
        };

        MODEL<ReadIOFile<ShortBuffer>> READ_SHORT = new MODEL<ReadIOFile<ShortBuffer>>() {

            public ReadIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ReadIOFile.SHORT);
            }
        };


        MODEL< WriteIOFile<LongBuffer>> WRITE_LONG = new MODEL< WriteIOFile<LongBuffer>>() {

            public WriteIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.LONG);
            }
        };
        MODEL<WriteIOFile<DoubleBuffer>> WRITE_DOUBLE = new MODEL<WriteIOFile<DoubleBuffer>>() {

            public WriteIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.DOUBLE);
            }
        };
        MODEL<WriteIOFile<FloatBuffer>> WRITE_FLOAT = new MODEL<WriteIOFile<FloatBuffer>>() {

            public WriteIOFile<FloatBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.FLOAT);
            }
        };
        MODEL<WriteIOFile<IntBuffer>> WRITE_INT = new MODEL<WriteIOFile<IntBuffer>>() {

            public WriteIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.INT);
            }
        };
        MODEL<WriteIOFile<CharBuffer> > WRITE_CHAR = new MODEL<WriteIOFile<CharBuffer> >() {

            public WriteIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.CHAR);
            }
        };
        MODEL<WriteIOFile<ByteBuffer> > WRITE_BYTE = new MODEL<WriteIOFile<ByteBuffer> >() {

            public WriteIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.BYTE);
            }
        };

        MODEL<WriteIOFile<ShortBuffer>> WRITE_SHORT = new MODEL<WriteIOFile<ShortBuffer>>() {

            public WriteIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, WriteIOFile.SHORT);
            }
        };

        MODEL<EditIOFile<LongBuffer>> EDIT_LONG = new MODEL<EditIOFile<LongBuffer>>() {

            public EditIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.LONG);
            }
        };
        MODEL<EditIOFile<DoubleBuffer>> EDIT_DOUBLE = new MODEL<EditIOFile<DoubleBuffer>>() {

            public EditIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.DOUBLE);
            }
        };
        MODEL<EditIOFile<FloatBuffer>> EDIT_FLOAT = new MODEL<EditIOFile<FloatBuffer>>() {

            public EditIOFile<FloatBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.FLOAT);
            }
        };
        MODEL<EditIOFile<IntBuffer>> EDIT_INT = new MODEL<EditIOFile<IntBuffer>>() {

            public EditIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.INT);
            }
        };
        MODEL<EditIOFile<CharBuffer> > EDIT_CHAR = new MODEL<EditIOFile<CharBuffer> >() {

            public EditIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.CHAR);
            }
        };
        MODEL< EditIOFile<ByteBuffer> > EDIT_BYTE = new MODEL< EditIOFile<ByteBuffer> >() {

            public EditIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.BYTE);
            }
        };

        MODEL<EditIOFile<ShortBuffer>> EDIT_SHORT = new MODEL<EditIOFile<ShortBuffer>>() {

            public EditIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, EditIOFile.SHORT);
            }
        };

        F createIOFile(Connector connector, URI uri);
    }

    /**
     * 创建IO文件
     * @param connector 连接器
     * @param uri uri
     * @param model 模式
     * @return
     */
    public static <F> F createIOFile(Connector connector, URI uri , MODEL<F> model) {
        return model.createIOFile(connector, uri);
    }


    /**
     * @see MemoryConf
     * @param size
     * @throws MemorySetException
     */
    public static void setTotalMemSize(long size) throws MemorySetException {
        MemoryConf.setTotalMemSize(size);
    }

    /**
     * @see MemoryConf
     * @return
     */
    public static long getTotalMemSize() {
        return MemoryConf.getTotalMemSize();
    }

    /**
     * @see MemoryConf
     * @return
     */
    public final static long getMaxMemSizeForSet(){
        return MemoryConf.getMaxMemSizeForSet();
    }

    /**
     * @see MemoryConf
     * @return
     */
    public final static long getMinMemSizeForSet(){
        return MemoryConf.getMinMemSizeForSet();
    }


    /**
     * 设置内存超时检查时间
     * @param t
     * @return
     */
    public final static void setMemoryCheckSchedule(long t) {
        CacheManager.getInstance().resetTimer(t);
    }



    /**
     * 设置写线程数量
     * @param threads
     */
    public static void setSyncThreads(int threads){
        SyncManager.getInstance().setThreads(threads);
    }

    /**
     * 获取写线程数量
     * @return
     */
    public static int getSyncThreads(){
        return  SyncManager.getInstance().getThreads();
    }

    /**
     * 随机写入
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<DoubleBuffer> file, long p, double d) {
        IOFile.put(file, p, d);
    }
    /**
     * 随机写入
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<ByteBuffer> file, long p, byte d) {
        IOFile.put(file, p, d);
    }
    /**
     * 随机写入
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<CharBuffer> file, long p, char d) {
        IOFile.put(file, p, d);
    }
    /**
     * 随机写入
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<FloatBuffer> file, long p, float d) {
        IOFile.put(file, p, d);
    }
    /**
     * 随机写入
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<LongBuffer> file, long p, long d) {
        IOFile.put(file, p, d);
    }
    /**
     * 随机写入
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<IntBuffer> file, long p, int d) {
        IOFile.put(file, p, d);
    }
    /**
     * 随机写入
     * @param file
     * @param p
     * @param d
     */
    public static void put(IOFile<ShortBuffer> file, long p, short d) {
        IOFile.put(file, p, d);
    }


    /**
     * 连续写入
     * @param file
     * @param d
     */
    public static void put(IOFile<DoubleBuffer> file,  double d) {
        IOFile.put(file, d);
    }
    /**
     * 连续写入
     * @param file
     * @param d
     */
    public static void put(IOFile<ByteBuffer> file, byte d) {
        IOFile.put(file, d);
    }
    /**
     * 连续写入
     * @param file
     * @param d
     */
    public static void put(IOFile<CharBuffer> file,  char d) {
        IOFile.put(file,  d);
    }
    /**
     * 连续写入
     * @param file
     * @param d
     */
    public static void put(IOFile<FloatBuffer> file, float d) {
        IOFile.put(file,  d);
    }
    /**
     * 连续写入
     * @param file
     * @param d
     */
    public static void put(IOFile<LongBuffer> file,  long d) {
        IOFile.put(file, d);
    }
    /**
     * 连续写入
     * @param file
     * @param d
     */
    public static void put(IOFile<IntBuffer> file, int d) {
        IOFile.put(file,  d);
    }
    /**
     * 连续写入
     * @param file
     * @param d
     */
    public static void put(IOFile<ShortBuffer> file, short d) {
        IOFile.put(file, d);
    }


    /**
     * 随机读取
     * @param file
     * @param p
     * @return
     */
    public final static long getLong(IOFile<LongBuffer> file, long p) {
        return IOFile.getLong(file, p);
    }
    /**
     * 随机读取
     * @param file
     * @param p
     * @return
     */
    public final static int getInt(IOFile<IntBuffer> file, long p) {
        return IOFile.getInt(file, p);
    }
    /**
     * 随机读取
     * @param file
     * @param p
     * @return
     */
    public final static char getChar(IOFile<CharBuffer> file, long p) {
        return IOFile.getChar(file, p);
    }
    /**
     * 随机读取
     * @param file
     * @param p
     * @return
     */
    public final static double getDouble(IOFile<DoubleBuffer> file, long p) {
        return IOFile.getDouble(file, p);
    }
    /**
     * 随机读取
     * @param file
     * @param p
     * @return
     */
    public final static float getFloat(IOFile<FloatBuffer> file, long p) {
        return IOFile.getFloat(file, p);
    }
    /**
     * 随机读取
     * @param file
     * @param p
     * @return
     */
    public final static byte getByte(IOFile<ByteBuffer> file, long p) {
        return IOFile.getByte(file, p);
    }
    /**
     * 随机读取
     * @param file
     * @param p
     * @return
     */
    public final static short getShort(IOFile<ShortBuffer> file, long p) {
        return IOFile.getShort(file, p);
    }

}
