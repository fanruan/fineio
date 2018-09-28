package com.fineio.io.file;

import com.fineio.io.BaseBuffer;
import com.fineio.io.Buffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class ReadIOFile<B extends Buffer> extends BaseReadIOFile<B> {
    ReadIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
    }

    @Override
    protected FileLevel getFileLevel() {
        return FileLevel.READ;
    }

    public static final <E extends BaseBuffer> ReadIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new ReadIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer initBuffer(int index) {
        synchronized (this) {
            if (null == buffers[index]) {
                BaseBuffer buffer = model.createBuffer(connector, createIndexBlock(index), block_size_offset);
                buffers[index] = buffer.asRead();
            }
            return buffers[index];
        }
    }
}
