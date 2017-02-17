package com.fineio.io.write;

import com.fineio.file.FileBlock;
import com.fineio.io.Buffer;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/15.
 */
public abstract class WriteBuffer extends Buffer {

    private int byteLen;


    protected WriteBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

}
