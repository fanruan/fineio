package com.fineio.io.file;

import com.fineio.base.Bits;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.base.BufferKey;
import com.fineio.io.file.write.FileSyncManager;
import com.fineio.io.impl.BaseBuffer;
import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author yee
 * @date 2019/9/11
 */
public class WriteIOFile<B extends Buffer> extends IOFile<B> {
    private int offset;

    public WriteIOFile(Connector connector, URI uri, int offset) {
        super(connector, uri);
        this.offset = offset;
        this.blockSizeOffset = (byte) (connector.getBlockOffset() - offset);
        this.singleBlockLen = (1L << this.blockSizeOffset) - 1;
        createBufferArray(0);
    }

    public static <B extends Buffer> WriteIOFile<B> createFile(Connector connector, URI uri, int offset) {
        return new WriteIOFile<B>(connector, uri, offset);
    }

    @Override
    public void close() {
        if (close.compareAndSet(false, true)) {
            writeHead();
            List<Future> futures = new ArrayList<Future>(buffers.length);
            for (int i = 0; i < buffers.length; i++) {
                Future future = null;
                Buffer buffer = buffers[i];
                if (null != (future = writeBuffer(buffer))) {
                    futures.add(future);
                    buffers[i] = null;
                }
            }
            for (Future future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    FineIOLoggers.getLogger().error(e);
                }
            }
        }
    }

    private void writeHead() {
        FileBlock block = new FileBlock(uri, FileConstants.HEAD);
        byte[] bytes = new byte[HEAD_LEN];
        Bits.putInt(bytes, 0, buffers.length);
        bytes[STEP_LEN] = (byte) (blockSizeOffset + offset);
        try {
            connector.write(block, bytes);
        } catch (Throwable e) {
            FineIOLoggers.getLogger().error(e);
        }
    }

    public void put(int pos, byte v) {
        final int index = getIndex(pos);
        try {
            ((ByteBuffer) buffers[index]).putByte((int) (pos & singleBlockLen), v);
        } catch (NullPointerException e) {
            final ByteBuffer intUnsafeBuf = BaseBuffer.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(index))), blockSizeOffset);
            buffers[index] = intUnsafeBuf;
            intUnsafeBuf.putByte((int) (pos & singleBlockLen), v);
            writeIdx(index - 1);
        }
    }

    public void put(int pos, int v) {
        final int index = getIndex(pos);
        try {
            ((IntBuffer) buffers[index]).putInt((int) (pos & singleBlockLen), v);
        } catch (NullPointerException e) {
            final IntBuffer intUnsafeBuf = BaseBuffer.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(index))), blockSizeOffset).asInt();
            buffers[index] = intUnsafeBuf;
            intUnsafeBuf.putInt((int) (pos & singleBlockLen), v);
            writeIdx(index - 1);
        }
    }

    public void put(int pos, long v) {
        final int index = getIndex(pos);
        try {
            ((LongBuffer) buffers[index]).putLong((int) (pos & singleBlockLen), v);
        } catch (NullPointerException e) {
            final LongBuffer intUnsafeBuf = BaseBuffer.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(index))), blockSizeOffset).asLong();
            buffers[index] = intUnsafeBuf;
            intUnsafeBuf.putLong((int) (pos & singleBlockLen), v);
            writeIdx(index - 1);
        }
    }

    public void put(int pos, double v) {
        final int index = getIndex(pos);
        try {
            ((DoubleBuffer) buffers[index]).putDouble((int) (pos & singleBlockLen), v);
        } catch (NullPointerException e) {
            final DoubleBuffer intUnsafeBuf = BaseBuffer.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(index))), blockSizeOffset).asDouble();
            buffers[index] = intUnsafeBuf;
            intUnsafeBuf.putDouble((int) (pos & singleBlockLen), v);
            writeIdx(index - 1);
        }
    }

    private int getIndex(int pos) {
        final int idx = pos >> blockSizeOffset;
        if (idx >= buffers.length) {
            Buffer[] bufs = buffers;
            buffers = new Buffer[idx + 1];
            System.arraycopy(bufs, 0, buffers, 0, bufs.length);
        }
        return idx;
    }

    private Future writeBuffer(Buffer buf) {
        if (null != buf) {
            return FileSyncManager.getInstance().sync(buf);
        }
        return null;
    }

    private void writeIdx(int idx) {
        if (idx >= 0) {
            writeBuffer(buffers[idx]);
        }
    }
}
