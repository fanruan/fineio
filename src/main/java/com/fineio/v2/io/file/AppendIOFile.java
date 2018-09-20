package com.fineio.v2.io.file;

import com.fineio.storage.Connector;
import com.fineio.v2.FineIO;
import com.fineio.v2.cache.BufferPrivilege;
import com.fineio.v2.io.Buffer;
import com.fineio.v2.io.ByteBuffer;
import com.fineio.v2.io.CharBuffer;
import com.fineio.v2.io.DoubleBuffer;
import com.fineio.v2.io.FileModel;
import com.fineio.v2.io.FloatBuffer;
import com.fineio.v2.io.IntBuffer;
import com.fineio.v2.io.LongBuffer;
import com.fineio.v2.io.ShortBuffer;
import com.fineio.v2.io.read.ReadOnlyBuffer;
import com.fineio.v2.io.write.WriteOnlyBuffer;

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
        if (null != buffers && blocks > 0) {
            buffers[maxBlockIndex] = createBuffer(maxBlockIndex);
        }
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
        if (index < maxBlockIndex) {
            throw new UnsupportedOperationException(String.format("Index %d is unsupported in Append MODE"));
        }
        if (index == maxBlockIndex) {
            ReadOnlyBuffer buffer = model.createBufferForRead(connector, createIndexBlock(index), block_size_offset);
            buffer.flip();
        }
        return model.createBufferForWrite(connector, createIndexBlock(index), block_size_offset);
    }

    @Override
    protected BufferPrivilege getLevel() {
        return BufferPrivilege.APPEND;
    }

    @Override
    protected void closeChild(boolean clear) {
        if (buffers != null) {
            for (int i = 0; i < buffers.length; i++) {
                if (buffers[i] != null) {
                    if (clear) {
                        ((WriteOnlyBuffer) buffers[i]).forceAndClear();
                    } else {
                        ((WriteOnlyBuffer) buffers[i]).force();
                    }
                    buffers[i] = null;
                }
            }
        }
    }

    @Override
    public void put(long p, double d) {
        FineIO.put((IOFile<DoubleBuffer>) this, d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, byte d) {
        FineIO.put((IOFile<ByteBuffer>) this, d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, char d) {
        FineIO.put((IOFile<CharBuffer>) this, d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, float d) {
        FineIO.put((IOFile<FloatBuffer>) this, d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, long d) {
        FineIO.put((IOFile<LongBuffer>) this, d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, int d) {
        FineIO.put((IOFile<IntBuffer>) this, d);
    }

    /**
     * 随机写
     *
     * @param p
     * @param d
     */
    @Override
    public void put(long p, short d) {
        FineIO.put((IOFile<ShortBuffer>) this, d);
    }
}
