package com.fineio.v2.io.file;

import com.fineio.storage.Connector;
import com.fineio.v2.io.BaseBuffer;
import com.fineio.v2.io.Buffer;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class AppendIOFileV2<B extends Buffer> extends BaseReadIOFileV2<B> {
    private final boolean sync;

    AppendIOFileV2(Connector connector, URI uri, FileModel model, boolean sync) {
        super(connector, uri, model);
        this.sync = sync;
        int maxBlock = blocks - 1;
        if (maxBlock >= 0) {
            buffers[maxBlock] = initBuffer(maxBlock);
        }
    }

    public static final <E extends BaseBuffer> AppendIOFileV2<E> createFineIO(Connector connector, URI uri, FileModel model, boolean sync) {
        return new AppendIOFileV2<E>(connector, uri, model, sync);
    }

    @Override
    protected FileLevel getFileLevel() {
        return FileLevel.APPEND;
    }

    @Override
    protected Buffer initBuffer(int index) {

        synchronized (this) {
            Buffer buffer = buffers[index];
            if (buffer == null) {
                buffer = model.createBuffer(connector, createIndexBlock(index), block_size_offset, sync);
            } else {
                return buffer;
            }
            if (index == blocks - 1) {
                buffers[index] = buffer.asAppend();
            } else {
                buffers[index] = buffer.asWrite();
            }
            return buffers[index];
        }
    }

    @Override
    public final void close() {
        writeHeader();
        super.close();
    }
}
