package com.fineio.io.file;

import com.fineio.cache.LEVEL;
import com.fineio.io.Buffer;
import com.fineio.io.FileModel;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class ReadIOFile<T extends Buffer> extends AbstractReadIOFile<T> {

    ReadIOFile(Connector connector, URI uri, FileModel model) {
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
    public static final <E extends Buffer> ReadIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new ReadIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer createBuffer(int index) {
        return model.createBufferForRead(connector, createIndexBlock(index), block_size_offset);
    }

    @Override
    protected LEVEL getLevel() {
        return LEVEL.READ;
    }


    @Override
    protected void writeHeader() {
    }

    @Override
    protected void closeChild(boolean clear) {
        if (buffers != null) {
            for (int i = 0; i < buffers.length; i++) {
                if (buffers[i] != null) {
                    buffers[i].close();
                    buffers[i] = null;
                }
            }
        }
    }

    /**
     * 复制
     *
     * @return
     */
    public boolean copyTo(URI destUri) {
        synchronized (this) {
            try {
                if (buffers != null) {
                    URI destURI = URI.create(destUri.getPath() + "/");
                    connector.copy(createHeadBlock(), new FileBlock(destURI, FileConstants.HEAD));
                    for (int i = 0; i < buffers.length; i++) {
                        connector.copy(createIndexBlock(i), new FileBlock(destURI, String.valueOf(i)));
                    }
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
