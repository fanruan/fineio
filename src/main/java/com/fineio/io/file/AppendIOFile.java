package com.fineio.io.file;

import com.fineio.cache.BufferPrivilege;
import com.fineio.io.Buffer;
import com.fineio.io.FileModel;
import com.fineio.io.write.WriteOnlyBuffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/8/20
 */
public class AppendIOFile<T extends Buffer> extends AbstractReadIOFile<T> {
    private int maxBlockIndex;

    AppendIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
        maxBlockIndex = blocks - 1 < 0 ? 0 : blocks - 1;
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
    public static final <E extends Buffer> AppendIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new AppendIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer createBuffer(int index) {
        if (index == maxBlockIndex) {
            return model.createBufferForEdit(connector, createIndexBlock(index), block_size_offset);
        }
        return model.createBufferForWrite(connector, createIndexBlock(index), block_size_offset);
    }

    @Override
    protected BufferPrivilege getLevel() {
        return BufferPrivilege.EDITABLE;
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

    @Override
    public void put(long p, double d) {
        throw new UnsupportedOperationException();
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, byte d) {
        throw new UnsupportedOperationException();
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, char d) {
        throw new UnsupportedOperationException();
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, float d) {
        throw new UnsupportedOperationException();
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, long d) {
        throw new UnsupportedOperationException();
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, int d) {
        throw new UnsupportedOperationException();
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, short d) {
        throw new UnsupportedOperationException();
    }
}
