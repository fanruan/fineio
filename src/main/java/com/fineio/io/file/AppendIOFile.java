package com.fineio.io.file;

import com.fineio.io.BaseBuffer;
import com.fineio.io.Buffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class AppendIOFile<B extends Buffer> extends BaseReadIOFile<B> {
    AppendIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
        int maxBlock = blocks - 1;
        if (maxBlock >= 0) {
            buffers[maxBlock] = initBuffer(maxBlock);
        }
    }

    public static final <E extends BaseBuffer> AppendIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new AppendIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer initBuffer(int index) {

        synchronized (this) {
            Buffer buffer = buffers[index];
            if (buffer == null) {
                buffer = model.createBuffer(connector, createIndexBlock(index), block_size_offset);
            } else {
                return buffer;
            }
            if (index == blocks - 1) {
                buffers[index] = buffer.asRead().asWrite();
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
