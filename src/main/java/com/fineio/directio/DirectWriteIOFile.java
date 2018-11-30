package com.fineio.directio;

import com.fineio.io.Buffer;
import com.fineio.io.file.FileModel;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/10/2
 */
public final class DirectWriteIOFile<T extends Buffer> extends DirectIOFile<T> {
    private final boolean sync;

    DirectWriteIOFile(Connector connector, URI uri, FileModel model, boolean sync) {
        super(connector, uri, model);
        this.sync = sync;
    }

    /**
     * 创建File方法
     *
     * @param connector 连接器
     * @param uri       子路径
     * @param model     子类型
     * @param <E>       继承ReadBuffer的子类型
     * @return
     */
    public static final <E extends Buffer> DirectWriteIOFile<E> createFineIO(Connector connector, URI uri, FileModel model, boolean sync) {
        return new DirectWriteIOFile<E>(connector, uri, model, sync);
    }

    @Override
    protected Buffer initBuffer() {
        if (buffer == null) {
            synchronized (this) {
                buffer = model.createBuffer(connector, uri, sync).asWrite();
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


}
