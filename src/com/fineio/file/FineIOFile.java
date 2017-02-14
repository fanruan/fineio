package com.fineio.file;

import com.fineio.storage.Connector;
import com.fineio.exception.IOSetException;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public abstract class FineIOFile {

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



}
