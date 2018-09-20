package com.fineio.io.file;

import com.fineio.io.BaseBuffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class ReadIOFile<B extends BaseBuffer> extends BaseReadIOFile<B> {
    ReadIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
    }

    public static final <E extends BaseBuffer> ReadIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new ReadIOFile<E>(connector, uri, model);
    }

    @Override
    protected B initBuffer(int index) {
        B buffer = super.initBuffer(index);
        buffer.checkRead0();
        return buffer;
    }
}
