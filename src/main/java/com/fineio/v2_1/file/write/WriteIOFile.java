package com.fineio.v2_1.file.write;

import com.fineio.base.Bits;
import com.fineio.io.base.BufferKey;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileConstants;
import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;
import com.fineio.v2_1.file.IOFile;
import com.fineio.v2_1.unsafe.ByteUnsafeBuf;
import com.fineio.v2_1.unsafe.DoubleUnsafeBuf;
import com.fineio.v2_1.unsafe.IntUnsafeBuf;
import com.fineio.v2_1.unsafe.LongUnsafeBuf;
import com.fineio.v2_1.unsafe.UnsafeBuf;
import com.fineio.v2_1.unsafe.impl.BaseUnsafeBuf;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author yee
 * @date 2019/9/11
 */
public class WriteIOFile<B extends UnsafeBuf> extends IOFile<B> {
    private int offset;

    public WriteIOFile(Connector connector, URI uri, int offset) {
        super(connector, uri);
        this.offset = offset;
        this.buffers = new UnsafeBuf[16];
    }

    @Override
    public void close() throws IOException {
        if (close.compareAndSet(false, true)) {
            writeHead();
            List<Future> futures = new ArrayList<Future>(buffers.length);
            for (UnsafeBuf buffer : buffers) {
                Future future = null;
                if (null != buffer && null != (future = writeBuffer(buffer))) {
                    futures.add(future);
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
        Bits.putInt(bytes, 0, buffers == null ? 0 : buffers.length);
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
            ((ByteUnsafeBuf) buffers[index]).putByte((int) (pos & singleBlockLen), v);
        } catch (NullPointerException e) {
            final ByteUnsafeBuf intUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(index))), blockSizeOffset);
            buffers[index] = intUnsafeBuf;
            intUnsafeBuf.putByte((int) (pos & singleBlockLen), v);
            writeIdx(index - 1);
        }
    }

    public void put(int pos, int v) {
        final int index = getIndex(pos);
        try {
            ((IntUnsafeBuf) buffers[index]).putInt((int) (pos & singleBlockLen), v);
        } catch (NullPointerException e) {
            final IntUnsafeBuf intUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(index))), blockSizeOffset).asInt();
            buffers[index] = intUnsafeBuf;
            intUnsafeBuf.putInt((int) (pos & singleBlockLen), v);
            writeIdx(index - 1);
        }
    }

    public void put(int pos, long v) {
        final int index = getIndex(pos);
        try {
            ((LongUnsafeBuf) buffers[index]).putLong((int) (pos & singleBlockLen), v);
        } catch (NullPointerException e) {
            final LongUnsafeBuf intUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(index))), blockSizeOffset).asLong();
            buffers[index] = intUnsafeBuf;
            intUnsafeBuf.putLong((int) (pos & singleBlockLen), v);
            writeIdx(index - 1);
        }
    }

    public void put(int pos, double v) {
        final int index = getIndex(pos);
        try {
            ((DoubleUnsafeBuf) buffers[index]).putDouble((int) (pos & singleBlockLen), v);
        } catch (NullPointerException e) {
            final DoubleUnsafeBuf intUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(index))), blockSizeOffset).asDouble();
            buffers[index] = intUnsafeBuf;
            intUnsafeBuf.putDouble((int) (pos & singleBlockLen), v);
            writeIdx(index - 1);
        }
    }

    private int getIndex(int pos) {
        final int idx = pos >> blockSizeOffset;
        if (idx > buffers.length) {
            UnsafeBuf[] bufs = buffers;
            buffers = new UnsafeBuf[idx + 1];
            System.arraycopy(bufs, 0, buffers, 0, bufs.length);
        }
        return idx;
    }

    private Future writeBuffer(UnsafeBuf buf) {
        if (null != buf) {
            return FileSyncManager.getInstance().sync(buf);
        }
        return null;
    }

    private void writeIdx(int idx) {
        if (idx > 0) {
            writeBuffer(buffers[idx]);
        }
    }
}
