package com.fineio;


import com.fineio.file.FineReadIOFile;
import com.fineio.exception.ConstructException;
import com.fineio.exception.MemorySetException;
import com.fineio.file.FineIOFile;
import com.fineio.file.FineWriteIOFile;
import com.fineio.io.Buffer;
import com.fineio.io.read.*;
import com.fineio.io.write.WriteBuffer;
import com.fineio.memory.MemoryConf;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final  class FineIO {

    private static Constructor<FineWriteIOFile> CONS_WRITE ;

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


         MODEL<WriteBuffer> WRITE = new MODEL<WriteBuffer>() {

             @Override
             public FineWriteIOFile createIOFile(Connector connector, URI uri) {
                 return  createWriteIOFile(connector, uri);
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


    static {
        try {
            CONS_WRITE = FineWriteIOFile.class.getDeclaredConstructor(Connector.class, URI.class);
            CONS_WRITE.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ConstructException(e);
        }
    }


    /**
     * 创建写的文件
     * @param connector
     * @param uri
     * @return
     */
    private static FineWriteIOFile createWriteIOFile(Connector connector, URI uri) {
        try {
            return CONS_WRITE.newInstance(connector, uri);
        } catch (InstantiationException e) {
            throw new ConstructException(e);
        } catch (IllegalAccessException e) {
            throw new ConstructException(e);
        } catch (InvocationTargetException e) {
            throw new ConstructException(e);
        }
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
