package com.fineio.v2_1.file;

import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;
import com.fineio.v2_1.unsafe.UnsafeBuf;

import java.io.Closeable;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yee
 * @date 2019/9/11
 */
public abstract class IOFile<B extends UnsafeBuf> implements Closeable {
    protected final static int STEP_LEN = MemoryConstants.STEP_LONG;
    protected final static int HEAD_LEN = STEP_LEN + 1;
    protected UnsafeBuf[] buffers;
    protected Connector connector;
    protected URI uri;
    protected AtomicBoolean close = new AtomicBoolean(false);
    /**
     * 分多少块
     */
    protected int blocks;
    /**
     * 每块尺寸的大小的偏移量 2的N次方
     */
    protected byte blockSizeOffset;
    /**
     * 单个block的大小
     */
    protected long singleBlockLen;

    protected IOFile(Connector connector, URI uri) {
        this.connector = connector;
        this.uri = uri;
    }

    protected final void createBufferArray(int size) {
        this.blocks = size;
        this.buffers = new UnsafeBuf[size];
    }
}
