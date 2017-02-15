package com.fineio.file;

import com.fineio.exception.BufferConstructException;
import com.fineio.io.Buffer;
import com.fineio.storage.Connector;
import com.fineio.exception.IOSetException;

import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public abstract class FineIOFile<E extends Buffer> {

    protected URI uri;
    protected Connector connector;
    protected long blocks;
    protected long block_size;


    FineIOFile(Connector connector, URI uri) {
        if(uri == null || connector == null){
            throw new IOSetException("uri  or connector can't be null");
        }
        this.connector = connector;
        this.uri = uri;
    }


    public <T extends E> T createBuffer(Class<T> clazz, int index) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(Connector.class, FileBlock.class);
            constructor.setAccessible(true);
            return constructor.newInstance(connector, new FileBlock(uri, String.valueOf(index)));
        } catch (Exception e) {
            throw new BufferConstructException(e);
        }
    }

}
