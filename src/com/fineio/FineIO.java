package com.fineio;


import com.fineio.file.FineReadIOFile;
import com.fineio.exception.ConstructException;
import com.fineio.exception.MemorySetException;
import com.fineio.file.FineIOFile;
import com.fineio.file.FineWriteIOFile;
import com.fineio.memory.MemoryConf;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final  class FineIO {

    private static Constructor<FineReadIOFile> CONS_READ ;
    private static Constructor<FineWriteIOFile> CONS_WRITE ;

    static {
        try {
            CONS_READ = FineReadIOFile.class.getDeclaredConstructor(Connector.class, URI.class);
            CONS_READ.setAccessible(true);
            CONS_WRITE = FineWriteIOFile.class.getDeclaredConstructor(Connector.class, URI.class);
            CONS_WRITE.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ConstructException(e);
        }
    }


    /**
     * 创建读的文件
     * @param connector
     * @param uri
     * @return
     */
    public static FineIOFile createReadIOFile(Connector connector, URI uri) {
        try {
            return CONS_READ.newInstance(connector, uri);
        } catch (InstantiationException e) {
            throw new ConstructException(e);
        } catch (IllegalAccessException e) {
            throw new ConstructException(e);
        } catch (InvocationTargetException e) {
            throw new ConstructException(e);
        }
    }


    /**
     * 创建写的文件
     * @param connector
     * @param uri
     * @return
     */
    public static FineIOFile createWriteIOFile(Connector connector, URI uri) {
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
