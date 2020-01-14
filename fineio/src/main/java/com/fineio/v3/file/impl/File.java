package com.fineio.v3.file.impl;

import com.fineio.accessor.file.IFile;
import com.fineio.base.Bits;
import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
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
    protected static final String OLD_META = "head";
    protected static final int META_BYTES = 9;

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
        FileBlock oldMetaFileKey = new FileBlock(fileBlock.getPath(), OLD_META);
        if (connector.exists(metaFileKey)) {
            try (InputStream input = connector.read(metaFileKey)) {
                byte[] bytes = new byte[META_BYTES];
                final int read = input.read(bytes);
                ByteBuffer buf = ByteBuffer.wrap(bytes);
                blockOffset = buf.get();
                return buf.getLong() >> offset.getOffset();
            } catch (IOException e) {
                FineIOLoggers.getLogger().error(e);
            }
        } else if (connector.exists(oldMetaFileKey)) {
            // 兼容以前的lastPos
            try (InputStream input = connector.read(oldMetaFileKey)) {
                byte[] bytes = new byte[META_BYTES];
                final int read = input.read(bytes);
                long lastBlockIndex = Bits.getInt(bytes, 0) - 1;
                blockOffset = bytes[MemoryConstants.STEP_LONG];
                long lastBlockPos = getLastPos(new FileBlock(fileBlock.getPath(), String.valueOf(lastBlockIndex)));
                long lastPos = lastBlockIndex * (1 << (blockOffset - offset.getOffset())) + lastBlockPos;
                writeMeta(lastPos);
                return lastPos;
            } catch (IOException e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
        return 0;
    }

    private long getLastPos(FileBlock fileBlock) throws IOException {
        try (InputStream input = new BufferedInputStream(connector.read(fileBlock))) {
            try (ByteArrayOutputStream byteOutput = new ByteArrayOutputStream()) {
                IOUtils.copyBinaryTo(input, byteOutput);
                long size = byteOutput.size();
                return size >> offset.getOffset();
            }
        } catch (IOException e) {
            throw new FileNotFoundException("File not found " + fileBlock.toString());
        }
    }

    protected void writeMeta(long lastPos) {
        byte[] bytes = ByteBuffer.allocate(META_BYTES)
                .put(blockOffset)
                .putLong(lastPos << offset.getOffset()).array();
        try {
            connector.write(new FileBlock(fileBlock.getPath(), META), bytes);
        } catch (IOException e) {
            FineIOLoggers.getLogger().error(e);
        }
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