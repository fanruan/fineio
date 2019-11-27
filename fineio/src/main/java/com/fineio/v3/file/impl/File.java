package com.fineio.v3.file.impl;

import com.fineio.accessor.file.IFile;
import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author anchore
 * @date 2019/4/16
 */
public abstract class File<B extends DirectBuffer> implements Closeable, IFile<B> {
    /**
     * 1 byte: block offset
     * 1 long: last byte pos
     */
    protected static final String META = "meta";
    private static final int OLD_META_BYTES = Byte.BYTES + Integer.BYTES;
    protected static final int META_BYTES = Byte.BYTES + Long.BYTES;

    protected final FileBlock fileBlock;

    protected final Connector connector;
    protected final AtomicBoolean closed = new AtomicBoolean(false);
    protected final Offset offset;
    protected byte blockOffset;
    protected B[] buffers;

    protected File(FileBlock fileBlock, Offset offset, Connector connector) {
        this.fileBlock = fileBlock;
        this.offset = offset;
        this.connector = connector;
        blockOffset = connector.getBlockOffset();
    }

    protected long initMetaAndGetLastPos() {
        FileBlock metaFileKey = new FileBlock(fileBlock.getPath(), META);
        if (connector.exists(metaFileKey)) {
            try (InputStream input = connector.read(metaFileKey)) {
                byte[] bytes = new byte[META_BYTES];
                final int read = input.read(bytes);
                ByteBuffer buf = ByteBuffer.wrap(bytes);
                if (read == bytes.length) {
                    blockOffset = buf.get();
                    return buf.getLong() >> offset.getOffset();
                } else if (read == OLD_META_BYTES) {
                    // 兼容以前int的lastPos
                    blockOffset = buf.get();
                    return buf.getInt() >> offset.getOffset();
                }
            } catch (IOException e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
        return 0;
    }

    protected int nthBuf(long pos) {
        return (int) (pos >> (blockOffset - offset.getOffset()));
    }

    protected int nthVal(long pos) {
        return (int) (pos & ((1L << blockOffset - offset.getOffset()) - 1));
    }

    protected void ensureOpen() {
        if (closed.get()) {
            throw new FileClosedException(fileBlock);
        }
    }

    @Override
    public boolean exists() {
        return connector.exists(new FileBlock(fileBlock.getPath(), META));
    }

    @Override
    public abstract void close();
}