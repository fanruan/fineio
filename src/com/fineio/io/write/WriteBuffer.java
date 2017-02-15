package com.fineio.io.write;

import com.fineio.file.FileBlock;
import com.fineio.io.Buffer;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/15.
 */
public class WriteBuffer extends Buffer {

    protected WriteBuffer(Connector connector, FileBlock block) {
        super(connector, block);
    }
}
