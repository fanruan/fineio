package com.fineio.v21.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileConstants;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;
import com.fineio.v21.unsafe.UnsafeBuf;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
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

    protected void readHeader(int offset) {
        InputStream is = null;
        FileBlock head = new FileBlock(uri, FileConstants.HEAD);
        try {
            is = this.connector.read(head);
            if (is == null) {
                throw new BlockNotFoundException("block:" + uri.toString() + " not found!");
            }
            byte[] header = new byte[9];
            is.read(header);
            initBufferArray(offset, header);
        } catch (Throwable e) {
            throw new BlockNotFoundException("block:" + uri.toString() + " not found!");
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            singleBlockLen = (1L << blockSizeOffset) - 1;
        }
    }

    private void initBufferArray(int offset, byte[] header) {
        int p = 0;
        createBufferArray(Bits.getInt(header, p));
        //先空个long的位置
        p += IOFile.STEP_LEN;
        blockSizeOffset = (byte) (header[p] - offset);
    }
}
