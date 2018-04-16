package com.fineio.io.file;

import com.fineio.io.*;
import com.fineio.io.pool.BufferObservable;
import com.fineio.io.pool.BufferPool;
import com.fineio.io.pool.PoolMode;
import com.fineio.io.read.*;
import com.fineio.observer.FineIOObserver;
import com.fineio.storage.Connector;

import java.net.URI;
import java.util.concurrent.locks.Lock;

/**
 * Created by daniel on 2017/2/9.
 */
public final class ReadIOFile<T extends Buffer> extends AbstractReadIOFile<T> implements FineIOObserver<BufferObservable> {

    public static final ReadModel<ByteBuffer> BYTE = ByteReadBuffer.MODEL;

    public static final ReadModel<DoubleBuffer> DOUBLE = DoubleReadBuffer.MODEL;

    public static final ReadModel<LongBuffer> LONG = LongReadBuffer.MODEL;

    public static final ReadModel<IntBuffer> INT = IntReadBuffer.MODEL;

    public static final ReadModel<FloatBuffer> FLOAT = FloatReadBuffer.MODEL;

    public static final ReadModel<CharBuffer> CHAR = CharReadBuffer.MODEL;

    public static final ReadModel<ShortBuffer> SHORT = ShortReadBuffer.MODEL;

    private PoolMode poolMode;

    private ReadIOFile(Connector connector, URI uri, ReadModel<T> model) {
        super(connector, uri, model);
        poolMode = model.getPoolMode();
    }


    @Override
    protected FileBlock createIndexBlock(int index) {
        FileBlock block = super.createIndexBlock(index);
        BufferPool.getInstance(poolMode).registerFromIOFile(block.getBlockURI(), this);
        return block;
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
    public static final <E extends Buffer> ReadIOFile<E> createFineIO(Connector connector, URI uri, ReadModel<E> model) {
        return new ReadIOFile<E>(connector, uri, model);
    }

    protected void writeHeader() {
        //doNothing
    }

    public void update(BufferObservable observable) {
        int block = observable.getBlock();
        if (-1 != block && block < buffers.length) {
            switch (observable.getState()) {
                case CHANGE:
                    buffers[block] = (T) observable.getBuffer();
                    break;
                case CLEAR:
                    buffers[block] = null;
                    break;
                default:
                    break;
            }
        }
    }

}
