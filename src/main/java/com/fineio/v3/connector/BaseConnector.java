package com.fineio.v3.connector;

/**
 * @author yee
 */
public abstract class BaseConnector implements Connector {

    private final int blockOffset;

    /**
     * Default constructor
     *
     * @param blockOffset
     */
    public BaseConnector(int blockOffset) {
        this.blockOffset = blockOffset;
    }

    public BaseConnector() {
        this(22);
    }

    @Override
    public int getBlockOffset() {
        return blockOffset;
    }
}