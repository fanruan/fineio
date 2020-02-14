package com.fineio.v3.connector;

import com.fineio.accessor.Block;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.v3.Connector;

/**
 * @author yee
 */
public abstract class BaseConnector implements Connector {

    private final byte blockOffset;

    /**
     * Default constructor
     *
     * @param blockOffset
     */
    public BaseConnector(byte blockOffset) {
        this.blockOffset = blockOffset;
    }

    public BaseConnector() {
        this((byte) 22);
    }

    @Override
    public boolean exists(FileBlock block) {
        return exists((Block) block);
    }

    @Override
    public boolean delete(FileBlock block) {
        return delete((Block) block);
    }

    @Override
    public byte getBlockOffset() {
        return blockOffset;
    }
}