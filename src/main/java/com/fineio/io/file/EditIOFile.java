package com.fineio.io.file;

import com.fineio.cache.BufferPrivilege;
import com.fineio.io.Buffer;
import com.fineio.io.FileModel;
import com.fineio.io.edit.EditBuffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
@Deprecated
public final class EditIOFile<T extends Buffer> extends AbstractReadIOFile<T> {

    EditIOFile(Connector connector, URI uri, FileModel model) {
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
    public static final <E extends Buffer> EditIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new EditIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer createBuffer(int index) {
        return model.createBufferForEdit(connector, createIndexBlock(index), block_size_offset);
    }

    @Override
    protected BufferPrivilege getLevel() {
        return BufferPrivilege.EDITABLE;
    }

    @Override
    protected void closeChild(boolean clear) {
        if (buffers != null) {
            for (int i = 0; i < buffers.length; i++) {
                if (buffers[i] != null) {
                    if (clear) {
                        ((EditBuffer) buffers[i]).forceAndClear();
                    } else {
                        ((EditBuffer) buffers[i]).force();
                    }
                    buffers[i] = null;
                }
            }
        }
    }


}
