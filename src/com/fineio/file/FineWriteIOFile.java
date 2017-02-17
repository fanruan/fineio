package com.fineio.file;

import com.fineio.io.write.WriteBuffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class FineWriteIOFile<T extends WriteBuffer> extends  FineIOFile<T> {

    FineWriteIOFile(Connector connector, URI uri, Class<T> clazz){
        super(connector, uri, clazz);
        this.block_size_offset = (byte) (connector.getBlockOffset() - getOffset());
    }


}
