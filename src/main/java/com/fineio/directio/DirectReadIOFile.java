package com.fineio.directio;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v2.io.Buffer;
import com.fineio.v2.io.FileModel;

import java.net.URI;

/**
 * Created by daniel on 2017/4/25.
 */
public class DirectReadIOFile<T extends Buffer> extends DirectIOFile<T> {


    private DirectReadIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
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
    public static final <E extends Buffer> DirectReadIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new DirectReadIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer initBuffer() {
        if (buffer == null) {
            synchronized (this) {
                buffer = model.createBufferForRead(connector, uri);
            }
        }
        return buffer;
    }

    @Override
    protected void closeChild() {
        if (buffer != null) {
            buffer.closeWithOutSync();
        }
    }

    public boolean delete() {
        synchronized (this) {
            if (!released) {
                if (buffer != null) {
                    buffer.closeWithOutSync();
                    buffer = null;
                }
                released = true;
            }
        }
        return connector.delete(new FileBlock(uri));
    }

}
