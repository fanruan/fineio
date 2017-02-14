package com.fineio.file;

import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class FineWriteIOFile extends  FineIOFile {

    FineWriteIOFile(Connector connector, URI uri){
        super(connector, uri);
        this.block_size = connector.getBlockSize();
    }
}
