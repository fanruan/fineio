package com.fineio.directio;

import com.fineio.io.Buffer;
import com.fineio.io.FileModel;
import com.fineio.io.edit.EditBuffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class DirectEditIOFile<T extends Buffer> extends DirectIOFile<T> {


    DirectEditIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
    }

    /**
     * 创建File方法
     *
     * @param connector 连接器
     * @param uri       子路径
     * @param model     子类型
     * @param <E>       继承EditBuffer的子类型
     * @return
     */
    public static final <E extends Buffer> DirectEditIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new DirectEditIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer initBuffer() {
        if (buffer == null) {
            synchronized (this) {
                buffer = model.createBufferForEdit(connector, uri);
            }
        }
        return buffer;
    }

    @Override
    protected void closeChild() {
        if (buffer != null) {
            ((EditBuffer) buffer).force();
        }
    }


}
