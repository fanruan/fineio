package com.fineio.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferConstructException;
import com.fineio.io.Buffer;
import com.fineio.io.read.ReadBuffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class FineReadIOFile extends FineIOFile<ReadBuffer> {

    FineReadIOFile(Connector connector, URI uri){
        super(connector, uri);
        readHeader();
    }

    private void readHeader() {
        byte[] header = this.connector.read(new FileBlock(uri, FileConstants.HEAD));
        if(header == null){
            throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
        }
        int p = 0;
        blocks = Bits.getLong(header, p);
        p += MemoryConstants.STEP_LONG;
        block_size = Bits.getLong(header, p);
    }

    public String getPath(){
        return uri.getPath();
    }

}
