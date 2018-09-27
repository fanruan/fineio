package com.fineio.io.file;

import com.fineio.cache.BufferPrivilege;
import com.fineio.io.Buffer;
import com.fineio.io.FileModel;
import com.fineio.io.write.WriteOnlyBuffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class WriteIOFile<T extends Buffer> extends IOFile<T> {

    WriteIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
        this.block_size_offset = (byte) (connector.getBlockOffset() - model.offset());
        single_block_len = (1L << block_size_offset) - 1;
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
    public static final <E extends Buffer> WriteIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new WriteIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer createBuffer(int index) {
        return model.createBufferForWrite(connector, createIndexBlock(index), block_size_offset);
    }

    @Override
    protected BufferPrivilege getLevel() {
        return BufferPrivilege.WRITABLE;
    }

    @Override
    protected void closeChild(boolean clear) {
        if (buffers != null) {
            for (int i = 0; i < buffers.length; i++) {
                if (buffers[i] != null && null != buffers[i].get()) {
                    if (clear) {
                        ((WriteOnlyBuffer) buffers[i].get()).forceAndClear();
                    } else {
                        ((WriteOnlyBuffer) buffers[i].get()).force();
                    }
                    buffers[i] = null;
                }
            }
        }
    }

}
