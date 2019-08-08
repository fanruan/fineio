package com.fineio.io.base;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/3/7.
 */
public final class BufferKey {

    /**
     * 不可改变的对象
     */
    private final Connector connector;

    private final FileBlock block;

    public BufferKey(Connector connector, FileBlock block) {
        this.connector = connector;
        this.block = block;
    }

    public Connector getConnector() {
        return connector;
    }

    public FileBlock getBlock() {
        return block;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BufferKey bufferKey = (BufferKey) o;
        return connector == bufferKey.connector
                && (block != null ? block.equals(bufferKey.block) : bufferKey.block == null);
    }

    @Override
    public int hashCode() {
        int result = connector != null ? connector.hashCode() : 0;
        result = 31 * result + (block != null ? block.hashCode() : 0);
        return result;
    }
}
