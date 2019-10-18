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
     * 1 int: last byte pos 单个file最大支持约21.4亿行byte，5.3亿行int，2.6亿行long/double
     */
    protected static final String META = "meta";

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

    protected int initMetaAndGetLastPos() {
        FileBlock metaFileKey = new FileBlock(fileBlock.getPath(), META);
        if (connector.exists(metaFileKey)) {
            try (InputStream input = connector.read(metaFileKey)) {
                byte[] bytes = new byte[5];
                if (input.read(bytes) == bytes.length) {
                    ByteBuffer buf = ByteBuffer.wrap(bytes);
                    blockOffset = buf.get();
                    return buf.getInt() >> offset.getOffset();
                }
            } catch (IOException e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
        return 0;
    }

    protected int nthBuf(int pos) {
        return pos >> (blockOffset - offset.getOffset());
    }

    protected int nthVal(int pos) {
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