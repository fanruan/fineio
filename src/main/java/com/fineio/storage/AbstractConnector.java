package com.fineio.storage;

import com.fineio.io.file.FileBlock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Created by daniel on 2017/3/1.
 */
public abstract class AbstractConnector implements Connector {

    protected final static byte DEFAULT_OFFSET = 22;


    /**
     * 转换一下
     * @param file
     * @param bytes
     */
    @Override
    public void write(FileBlock file, byte[] bytes) throws IOException {
        write(file, new ByteArrayInputStream(bytes));
    }


    /**
     * 默认值22;
     * @return
     */
    @Override
    public byte getBlockOffset() {
        return DEFAULT_OFFSET;
    }

    @Override
    public URI deleteParent(FileBlock block) {
        return null;
    }
}
