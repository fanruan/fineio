package com.fineio.io.edit;

import com.fineio.file.FileBlock;
import com.fineio.io.write.WriteBuffer;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/20.
 */
public abstract class EditBuffer extends WriteBuffer implements Edit {

    private volatile boolean load = false;

    protected int max_byte_len;

    protected EditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }
}
