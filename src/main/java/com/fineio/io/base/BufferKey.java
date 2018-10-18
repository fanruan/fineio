package com.fineio.io.base;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;

public final class BufferKey {
    private final Connector connector;
    private final FileBlock block;

    BufferKey(final Connector connector, final FileBlock block) {
        this.connector = connector;
        this.block = block;
    }

    public Connector getConnector() {
        return this.connector;
    }

    public FileBlock getBlock() {
        return this.block;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final BufferKey bufferKey = (BufferKey) o;
        return this.connector == bufferKey.connector && ((this.block == null) ? (bufferKey.block == null) : this.block.equals(bufferKey.block));
    }

    @Override
    public int hashCode() {
        return 31 * ((this.connector != null) ? this.connector.hashCode() : 0) + ((this.block != null) ? this.block.hashCode() : 0);
    }
}
