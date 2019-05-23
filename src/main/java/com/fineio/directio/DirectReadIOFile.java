package com.fineio.directio;

import com.fineio.io.Buffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileModel;
import com.fineio.io.file.writer.JobFinishedManager;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/10/2
 */
public final class DirectReadIOFile<B extends Buffer> extends DirectIOFile<B> {

    DirectReadIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
        initBuffer();
    }

    public static final <E extends Buffer> DirectReadIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new DirectReadIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer initBuffer() {
        if (buffer == null) {
            synchronized (this) {
                buffer = model.createBuffer(connector, uri, false).asRead();
            }
        }
        return buffer;
    }

    @Override
    protected void closeChild() {
        if (buffer != null) {
            buffer.clearAfterClose();
            buffer = null;
        }
    }

    public void delete() {
        JobFinishedManager.getInstance().finish(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    if (!released) {
                        closeChild();
                        released = true;
                    }
                    connector.delete(new FileBlock(uri.getPath()));
                }
            }
        });
    }
}
