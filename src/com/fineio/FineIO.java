package com.fineio;


import com.fineio.exception.MemorySetException;
import com.fineio.file.EditIOFile;
import com.fineio.file.IOFile;
import com.fineio.file.ReadIOFile;
import com.fineio.file.WriteIOFile;
import com.fineio.io.Buffer;
import com.fineio.io.edit.*;
import com.fineio.io.read.*;
import com.fineio.io.write.*;
import com.fineio.memory.MemoryConf;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final  class FineIO {

    public interface MODEL<T extends Buffer> {
         MODEL<LongReadBuffer> READ_LONG = new MODEL<LongReadBuffer>() {

             @Override
             public ReadIOFile<LongReadBuffer> createIOFile(Connector connector, URI uri) {
                 return ReadIOFile.createFineIO(connector, uri, LongReadBuffer.class);
             }
         };
        MODEL<DoubleReadBuffer> READ_DOUBLE = new MODEL<DoubleReadBuffer>() {

            @Override
            public ReadIOFile<DoubleReadBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, DoubleReadBuffer.class);
            }
        };
        MODEL<IntReadBuffer> READ_INT = new MODEL<IntReadBuffer>() {

            @Override
            public ReadIOFile<IntReadBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, IntReadBuffer.class);
            }
        };
        MODEL<CharReadBuffer> READ_CHAR = new MODEL<CharReadBuffer>() {

            @Override
            public ReadIOFile<CharReadBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, CharReadBuffer.class);
            }
        };
        MODEL<ByteReadBuffer> READ_BYTE = new MODEL<ByteReadBuffer>() {

            @Override
            public ReadIOFile<ByteReadBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ByteReadBuffer.class);
            }
        };

        MODEL<ShortReadBuffer> READ_SHORT = new MODEL<ShortReadBuffer>() {

            @Override
            public ReadIOFile<ShortReadBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ShortReadBuffer.class);
            }
        };


        MODEL<LongWriteBuffer> WRITE_LONG = new MODEL<LongWriteBuffer>() {

            @Override
            public WriteIOFile<LongWriteBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, LongWriteBuffer.class);
            }
        };
        MODEL<DoubleWriteBuffer> WRITE_DOUBLE = new MODEL<DoubleWriteBuffer>() {

            @Override
            public WriteIOFile<DoubleWriteBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, DoubleWriteBuffer.class);
            }
        };
        MODEL<IntWriteBuffer> WRITE_INT = new MODEL<IntWriteBuffer>() {

            @Override
            public WriteIOFile<IntWriteBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, IntWriteBuffer.class);
            }
        };
        MODEL<CharWriteBuffer> WRITE_CHAR = new MODEL<CharWriteBuffer>() {

            @Override
            public WriteIOFile<CharWriteBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, CharWriteBuffer.class);
            }
        };
        MODEL<ByteWriteBuffer> WRITE_BYTE = new MODEL<ByteWriteBuffer>() {

            @Override
            public WriteIOFile<ByteWriteBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, ByteWriteBuffer.class);
            }
        };

        MODEL<ShortWriteBuffer> WRITE_SHORT = new MODEL<ShortWriteBuffer>() {

            @Override
            public WriteIOFile<ShortWriteBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, ShortWriteBuffer.class);
            }
        };

        MODEL<LongEditBuffer> EDIT_LONG = new MODEL<LongEditBuffer>() {

            @Override
            public EditIOFile<LongEditBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, LongEditBuffer.class);
            }
        };
        MODEL<DoubleEditBuffer> EDIT_DOUBLE = new MODEL<DoubleEditBuffer>() {

            @Override
            public EditIOFile<DoubleEditBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, DoubleEditBuffer.class);
            }
        };
        MODEL<IntEditBuffer> EDIT_INT = new MODEL<IntEditBuffer>() {

            @Override
            public EditIOFile<IntEditBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, IntEditBuffer.class);
            }
        };
        MODEL<CharEditBuffer> EDIT_CHAR = new MODEL<CharEditBuffer>() {

            @Override
            public EditIOFile<CharEditBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, CharEditBuffer.class);
            }
        };
        MODEL<ByteEditBuffer> EDIT_BYTE = new MODEL<ByteEditBuffer>() {

            @Override
            public EditIOFile<ByteEditBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, ByteEditBuffer.class);
            }
        };

        MODEL<ShortEditBuffer> EDIT_SHORT = new MODEL<ShortEditBuffer>() {

            @Override
            public EditIOFile<ShortEditBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, ShortEditBuffer.class);
            }
        };

        IOFile<T> createIOFile(Connector connector, URI uri);
    }

    /**
     * 创建IO文件
     * @param connector 连接器
     * @param uri uri
     * @param model 模式
     * @param <T>
     * @return
     */
    public static <T extends Buffer> IOFile<T> createIOFile(Connector connector, URI uri , MODEL<T> model) {
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
     * 保存的方法
     * @param file
     * @param p
     * @param d
     */
    public static void put(EditIOFile<DoubleEditBuffer> file, int p, double d) {
        EditIOFile.put(file, p, d);
    }

    public static void put(EditIOFile<ByteEditBuffer> file, int p, byte d) {
        EditIOFile.put(file, p, d);
    }

    public static void put(EditIOFile<CharEditBuffer> file, int p, char d) {
        EditIOFile.put(file, p, d);
    }

    public static void put(EditIOFile<FloatEditBuffer> file, int p, float d) {
        EditIOFile.put(file, p, d);
    }

    public static void put(EditIOFile<LongEditBuffer> file, int p, long d) {
        EditIOFile.put(file, p, d);
    }

    public static void put(EditIOFile<IntEditBuffer> file, int p, int d) {
        EditIOFile.put(file, p, d);
    }

    public static void put(EditIOFile<ShortEditBuffer> file, int p, short d) {
        EditIOFile.put(file, p, d);
    }

    public final static long getLong(EditIOFile<LongEditBuffer> file, long p) {
        return EditIOFile.getLong(file, p);
    }

    public final static int getInt(EditIOFile<IntEditBuffer> file, long p) {
        return EditIOFile.getInt(file, p);
    }

    public final static int getChar(EditIOFile<CharEditBuffer> file, long p) {
        return EditIOFile.getChar(file, p);
    }

    public final static double getDouble(EditIOFile<DoubleEditBuffer> file, long p) {
        return EditIOFile.getDouble(file, p);
    }

    public final static float getFloat(EditIOFile<FloatEditBuffer> file, long p) {
        return EditIOFile.getFloat(file, p);
    }

    public final static byte getByte(EditIOFile<ByteEditBuffer> file, long p) {
        return EditIOFile.getByte(file, p);
    }

    public final static short getShort(EditIOFile<ShortEditBuffer> file, long p) {
        return EditIOFile.getShort(file, p);
    }


    public final static long getLong(IOFile<LongReadBuffer> file, long p) {
        return ReadIOFile.getLong(file, p);
    }

    public final static int getInt(IOFile<IntReadBuffer> file, long p) {
        return ReadIOFile.getInt(file, p);
    }

    public final static int getChar(IOFile<CharReadBuffer> file, long p) {
        return ReadIOFile.getChar(file, p);
    }

    public final static double getDouble(IOFile<DoubleReadBuffer> file, long p) {
        return ReadIOFile.getDouble(file, p);
    }

    public final static float getFloat(IOFile<FloatReadBuffer> file, long p) {
        return ReadIOFile.getFloat(file, p);
    }

    public final static byte getByte(IOFile<ByteReadBuffer> file, long p) {
        return ReadIOFile.getByte(file, p);
    }

    public final static short getShort(IOFile<ShortReadBuffer> file, long p) {
        return ReadIOFile.getShort(file, p);
    }



    public static void put(IOFile<DoubleWriteBuffer> file, int p, double d) {
        WriteIOFile.put(file, p, d);
    }

    public static void put(IOFile<ByteWriteBuffer> file, int p, byte d) {
        WriteIOFile.put(file, p, d);
    }

    public static void put(IOFile<CharWriteBuffer> file, int p, char d) {
        WriteIOFile.put(file, p, d);
    }

    public static void put(IOFile<FloatWriteBuffer> file, int p, float d) {
        WriteIOFile.put(file, p, d);
    }

    public static void put(IOFile<LongWriteBuffer> file, int p, long d) {
        WriteIOFile.put(file, p, d);
    }

    public static void put(IOFile<IntWriteBuffer> file, int p, int d) {
        WriteIOFile.put(file, p, d);
    }

    public static void put(IOFile<ShortWriteBuffer> file, int p, short d) {
        WriteIOFile.put(file, p, d);
    }

}
