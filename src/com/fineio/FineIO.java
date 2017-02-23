package com.fineio;


import com.fineio.exception.MemorySetException;
import com.fineio.file.EditIOFile;
import com.fineio.file.IOFile;
import com.fineio.file.ReadIOFile;
import com.fineio.file.WriteIOFile;
import com.fineio.io.*;
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

    public interface MODEL<F> {
         MODEL<ReadIOFile<LongBuffer>> READ_LONG = new MODEL<ReadIOFile<LongBuffer>>() {

             @Override
             public ReadIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                 return ReadIOFile.createFineIO(connector, uri, LongBuffer.class);
             }
         };
        MODEL<ReadIOFile<DoubleBuffer>> READ_DOUBLE = new MODEL<ReadIOFile<DoubleBuffer>>() {

            @Override
            public ReadIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, DoubleBuffer.class);
            }
        };
        MODEL<ReadIOFile<IntBuffer>> READ_INT = new MODEL<ReadIOFile<IntBuffer>>() {

            @Override
            public ReadIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, IntBuffer.class);
            }
        };
        MODEL<ReadIOFile<CharBuffer> > READ_CHAR = new MODEL<ReadIOFile<CharBuffer> >() {

            @Override
            public ReadIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, CharBuffer.class);
            }
        };
        MODEL<ReadIOFile<ByteBuffer> > READ_BYTE = new MODEL<ReadIOFile<ByteBuffer> >() {

            @Override
            public ReadIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ByteBuffer.class);
            }
        };

        MODEL<ReadIOFile<ShortBuffer>> READ_SHORT = new MODEL<ReadIOFile<ShortBuffer>>() {

            @Override
            public ReadIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return ReadIOFile.createFineIO(connector, uri, ShortBuffer.class);
            }
        };


        MODEL< WriteIOFile<LongBuffer>> WRITE_LONG = new MODEL< WriteIOFile<LongBuffer>>() {

            @Override
            public WriteIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, LongBuffer.class);
            }
        };
        MODEL<WriteIOFile<DoubleBuffer>> WRITE_DOUBLE = new MODEL<WriteIOFile<DoubleBuffer>>() {

            @Override
            public WriteIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, DoubleBuffer.class);
            }
        };
        MODEL<WriteIOFile<IntBuffer>> WRITE_INT = new MODEL<WriteIOFile<IntBuffer>>() {

            @Override
            public WriteIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, IntBuffer.class);
            }
        };
        MODEL<WriteIOFile<CharBuffer> > WRITE_CHAR = new MODEL<WriteIOFile<CharBuffer> >() {

            @Override
            public WriteIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, CharBuffer.class);
            }
        };
        MODEL<WriteIOFile<ByteBuffer> > WRITE_BYTE = new MODEL<WriteIOFile<ByteBuffer> >() {

            @Override
            public WriteIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, ByteBuffer.class);
            }
        };

        MODEL<WriteIOFile<ShortBuffer>> WRITE_SHORT = new MODEL<WriteIOFile<ShortBuffer>>() {

            @Override
            public WriteIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return WriteIOFile.createFineIO(connector, uri, ShortBuffer.class);
            }
        };

        MODEL<EditIOFile<LongBuffer>> EDIT_LONG = new MODEL<EditIOFile<LongBuffer>>() {

            @Override
            public EditIOFile<LongBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, LongBuffer.class);
            }
        };
        MODEL<EditIOFile<DoubleBuffer>> EDIT_DOUBLE = new MODEL<EditIOFile<DoubleBuffer>>() {

            @Override
            public EditIOFile<DoubleBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, DoubleBuffer.class);
            }
        };
        MODEL<EditIOFile<IntBuffer>> EDIT_INT = new MODEL<EditIOFile<IntBuffer>>() {

            @Override
            public EditIOFile<IntBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, IntBuffer.class);
            }
        };
        MODEL<EditIOFile<CharBuffer> > EDIT_CHAR = new MODEL<EditIOFile<CharBuffer> >() {

            @Override
            public EditIOFile<CharBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, CharBuffer.class);
            }
        };
        MODEL< EditIOFile<ByteBuffer> > EDIT_BYTE = new MODEL< EditIOFile<ByteBuffer> >() {

            @Override
            public EditIOFile<ByteBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, ByteBuffer.class);
            }
        };

        MODEL<EditIOFile<ShortBuffer>> EDIT_SHORT = new MODEL<EditIOFile<ShortBuffer>>() {

            @Override
            public EditIOFile<ShortBuffer> createIOFile(Connector connector, URI uri) {
                return EditIOFile.createFineIO(connector, uri, ShortBuffer.class);
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

    public static void put(IOFile<DoubleBuffer> file, long p, double d) {
        IOFile.put(file, p, d);
    }

    public static void put(IOFile<ByteBuffer> file, long p, byte d) {
        IOFile.put(file, p, d);
    }

    public static void put(IOFile<CharBuffer> file, long p, char d) {
        IOFile.put(file, p, d);
    }

    public static void put(IOFile<FloatBuffer> file, long p, float d) {
        IOFile.put(file, p, d);
    }

    public static void put(IOFile<LongBuffer> file, long p, long d) {
        IOFile.put(file, p, d);
    }

    public static void put(IOFile<IntBuffer> file, long p, int d) {
        IOFile.put(file, p, d);
    }

    public static void put(IOFile<ShortBuffer> file, long p, short d) {
        IOFile.put(file, p, d);
    }


    public final static long getLong(IOFile<LongBuffer> file, long p) {
        return IOFile.getLong(file, p);
    }

    public final static int getInt(IOFile<IntBuffer> file, long p) {
        return IOFile.getInt(file, p);
    }

    public final static int getChar(IOFile<CharBuffer> file, long p) {
        return IOFile.getChar(file, p);
    }

    public final static double getDouble(IOFile<DoubleBuffer> file, long p) {
        return IOFile.getDouble(file, p);
    }

    public final static float getFloat(IOFile<FloatBuffer> file, long p) {
        return IOFile.getFloat(file, p);
    }

    public final static byte getByte(IOFile<ByteBuffer> file, long p) {
        return IOFile.getByte(file, p);
    }

    public final static short getShort(IOFile<ShortBuffer> file, long p) {
        return IOFile.getShort(file, p);
    }

}
