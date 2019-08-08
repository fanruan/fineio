package com.fineio.v3.connector;

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
    public byte getBlockOffset() {
        return blockOffset;
    }
}