package com.fineio.io.file;

import com.fineio.io.BaseBuffer;
import com.fineio.io.Level;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class AppendIOFile<B extends BaseBuffer> extends BaseReadIOFile<B> {
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
    protected B initBuffer(int index) {
        if (index == blocks - 1) {
            synchronized (this) {
                if (buffers[index] == null) {
                    buffers[index] = model.createBuffer(connector, createIndexBlock(index), block_size_offset);
                }
                if (buffers[index].getLevel() != Level.WRITE) {
                    buffers[index].checkRead0();
                    buffers[index].flip();
                }
                return (B) buffers[index];
            }
        }
        return super.initBuffer(index);
    }

    @Override
    public final void close() {
        writeHeader();
        super.close();
    }
}
