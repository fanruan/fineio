package com.fineio;


import com.fineio.file.FineReadIOFile;
import com.fineio.exception.ConstructException;
import com.fineio.exception.MemorySetException;
import com.fineio.file.FineIOFile;
import com.fineio.file.FineWriteIOFile;
import com.fineio.io.Buffer;
import com.fineio.io.read.*;
import com.fineio.io.write.*;
import com.fineio.memory.MemoryConf;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final  class FineIO {

    public interface MODEL<T extends Buffer> {
         MODEL<LongReadBuffer> READ_LONG = new MODEL<LongReadBuffer>() {

             @Override
             public FineReadIOFile<LongReadBuffer> createIOFile(Connector connector, URI uri) {
                 return FineReadIOFile.createFineIO(connector, uri, LongReadBuffer.class);
             }
         };
        MODEL<DoubleReadBuffer> READ_DOUBLE = new MODEL<DoubleReadBuffer>() {

            @Override
            public FineReadIOFile<DoubleReadBuffer> createIOFile(Connector connector, URI uri) {
                return FineReadIOFile.createFineIO(connector, uri, DoubleReadBuffer.class);
            }
        };
        MODEL<IntReadBuffer> READ_INT = new MODEL<IntReadBuffer>() {

            @Override
            public FineReadIOFile<IntReadBuffer> createIOFile(Connector connector, URI uri) {
                return FineReadIOFile.createFineIO(connector, uri, IntReadBuffer.class);
            }
        };
        MODEL<CharReadBuffer> READ_CHAR = new MODEL<CharReadBuffer>() {

            @Override
            public FineReadIOFile<CharReadBuffer> createIOFile(Connector connector, URI uri) {
                return FineReadIOFile.createFineIO(connector, uri, CharReadBuffer.class);
            }
        };
        MODEL<ByteReadBuffer> READ_BYTE = new MODEL<ByteReadBuffer>() {

            @Override
            public FineReadIOFile<ByteReadBuffer> createIOFile(Connector connector, URI uri) {
                return FineReadIOFile.createFineIO(connector, uri, ByteReadBuffer.class);
            }
        };

        MODEL<ShortReadBuffer> READ_SHORT = new MODEL<ShortReadBuffer>() {

            @Override
            public FineReadIOFile<ShortReadBuffer> createIOFile(Connector connector, URI uri) {
                return FineReadIOFile.createFineIO(connector, uri, ShortReadBuffer.class);
            }
        };


        MODEL<LongWriteBuffer> WRITE_LONG = new MODEL<LongWriteBuffer>() {

            @Override
            public FineWriteIOFile<LongWriteBuffer> createIOFile(Connector connector, URI uri) {
                return FineWriteIOFile.createFineIO(connector, uri, LongWriteBuffer.class);
            }
        };
        MODEL<DoubleWriteBuffer> WRITE_DOUBLE = new MODEL<DoubleWriteBuffer>() {

            @Override
            public FineWriteIOFile<DoubleWriteBuffer> createIOFile(Connector connector, URI uri) {
                return FineWriteIOFile.createFineIO(connector, uri, DoubleWriteBuffer.class);
            }
        };
        MODEL<IntWriteBuffer> WRITE_INT = new MODEL<IntWriteBuffer>() {

            @Override
            public FineWriteIOFile<IntWriteBuffer> createIOFile(Connector connector, URI uri) {
                return FineWriteIOFile.createFineIO(connector, uri, IntWriteBuffer.class);
            }
        };
        MODEL<CharWriteBuffer> WRITE_CHAR = new MODEL<CharWriteBuffer>() {

            @Override
            public FineWriteIOFile<CharWriteBuffer> createIOFile(Connector connector, URI uri) {
                return FineWriteIOFile.createFineIO(connector, uri, CharWriteBuffer.class);
            }
        };
        MODEL<ByteWriteBuffer> WRITE_BYTE = new MODEL<ByteWriteBuffer>() {

            @Override
            public FineWriteIOFile<ByteWriteBuffer> createIOFile(Connector connector, URI uri) {
                return FineWriteIOFile.createFineIO(connector, uri, ByteWriteBuffer.class);
            }
        };

        MODEL<ShortWriteBuffer> WRITE_SHORT = new MODEL<ShortWriteBuffer>() {

            @Override
            public FineWriteIOFile<ShortWriteBuffer> createIOFile(Connector connector, URI uri) {
                return FineWriteIOFile.createFineIO(connector, uri, ShortWriteBuffer.class);
            }
        };

        FineIOFile<T> createIOFile(Connector connector, URI uri);
    }

    /**
     * 创建IO文件
     * @param connector 连接器
     * @param uri uri
     * @param model 模式
     * @param <T>
     * @return
     */
    public static <T extends Buffer> FineIOFile<T> createIOFile(Connector connector, URI uri , MODEL<T> model) {
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

}
