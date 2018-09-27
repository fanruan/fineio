package com.fineio.io.file;


import com.fineio.io.BaseBuffer;
import com.fineio.io.Buffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class WriteIOFile<B extends Buffer> extends IOFile<B> {
    WriteIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
        this.block_size_offset = (byte) (connector.getBlockOffset() - model.offset());
        single_block_len = (1L << block_size_offset) - 1;
    }

    public static final <E extends BaseBuffer> WriteIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new WriteIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer initBuffer(int index) {
        synchronized (this) {
            if (null == buffers[index]) {
                BaseBuffer buffer = model.createBuffer(connector, createIndexBlock(index), block_size_offset);
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
