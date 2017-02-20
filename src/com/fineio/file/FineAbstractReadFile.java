package com.fineio.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.io.AbstractBuffer;
import com.fineio.io.Buffer;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by daniel on 2017/2/20.
 */
public abstract class FineAbstractReadFile<T extends Buffer> extends FineIOFile<T> {
    FineAbstractReadFile(Connector connector, URI uri, Class<T> clazz) {
        super(connector, uri, clazz);
        readHeader(getOffset());
    }


    private void readHeader(byte offset) {
        InputStream is  = this.connector.read(new FileBlock(uri, FileConstants.HEAD));
        if(is == null){
            throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
        }
        try {
            byte[] header = new byte[9];
            is.read(header);
            int p = 0;
            createBufferArray(Bits.getInt(header, p));
            //先空个long的位置
            p += MemoryConstants.STEP_LONG;
            block_size_offset = (byte) (header[p] - offset);
            single_block_len = (1L << block_size_offset) - 1;
        } catch (IOException e) {
            throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
        }
    }
}
