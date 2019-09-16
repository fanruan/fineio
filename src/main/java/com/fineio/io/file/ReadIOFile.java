package com.fineio.io.file;

import com.fineio.exception.BlockNotFoundException;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.base.BufferKey;
import com.fineio.io.impl.BaseBuffer;
import com.fineio.storage.Connector;
import com.fineio.v21.cache.CacheManager;

import java.net.URI;
import java.util.Arrays;

/**
 * @author yee
 * @date 2019/9/11
 */
public class ReadIOFile<B extends Buffer> extends IOFile<B> {
    private boolean access;

    protected ReadIOFile(Connector connector, URI uri, int offset) {
        super(connector, uri);
        readHeader(offset);
    }

    public static <B extends Buffer> ReadIOFile<B> createFile(final Connector connector, final URI uri, final int offset) {
        return CacheManager.getInstance().get(uri, new CacheManager.FileCreator<B>() {
            @Override
            public ReadIOFile<B> createFile() {
                return new ReadIOFile<B>(connector, uri, offset);
            }
        });
    }

    public int getInt(int pos) {
        try {
            if (!access) {
                access = true;
            }
            return ((IntBuffer) getBuffer(pos)).getInt((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final FileBlock block = new FileBlock(uri, String.valueOf(i));
            final BufferKey bufferKey = new BufferKey(connector, block);
            buffers[i] = CacheManager.getInstance().get(bufferKey.getBlock().getBlockURI(), new CacheManager.BufferCreator() {
                @Override
                public Buffer createBuffer() {
                    return BaseBuffer.newBuffer(bufferKey).asInt();
                }
            });
            return ((IntBuffer) buffers[i]).getInt((int) (pos & singleBlockLen));
        }
    }

    public byte getByte(int pos) {
        try {
            return ((ByteBuffer) getBuffer(pos)).getByte((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final ByteBuffer byteUnsafeBuf = BaseBuffer.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(i))));
            CacheManager.getInstance().put(byteUnsafeBuf);
            buffers[i] = byteUnsafeBuf;
            return byteUnsafeBuf.getByte((int) (pos & singleBlockLen));
        }
    }

    public long getLong(int pos) {
        try {
            return ((LongBuffer) getBuffer(pos)).getLong((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final FileBlock block = new FileBlock(uri, String.valueOf(i));
            final BufferKey bufferKey = new BufferKey(connector, block);

            buffers[i] = CacheManager.getInstance().get(bufferKey.getBlock().getBlockURI(), new CacheManager.BufferCreator() {
                @Override
                public Buffer createBuffer() {
                    return BaseBuffer.newBuffer(bufferKey).asLong();
                }
            });
            return ((LongBuffer) buffers[i]).getLong((int) (pos & singleBlockLen));
        }
    }

    public double getDouble(int pos) {
        try {
            return ((DoubleBuffer) getBuffer(pos)).getDouble((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final DoubleBuffer byteUnsafeBuf = BaseBuffer.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(i)))).asDouble();
            CacheManager.getInstance().put(byteUnsafeBuf);
            buffers[i] = byteUnsafeBuf;
            return byteUnsafeBuf.getDouble((int) (pos & singleBlockLen));
        }
    }

    private B getBuffer(int pos) {
        int idx = pos >> blockSizeOffset;
        if (idx < buffers.length && idx >= 0) {
            return (B) buffers[idx];
        }
        throw new BlockNotFoundException("block:" + uri.toString() + " not found!");
    }

    @Override
    public void close() {
        if (close.compareAndSet(false, true) && null != buffers) {
            Buffer[] bufs = buffers;
            CacheManager.getInstance().removeBuffers(uri, bufs.length);
            Arrays.fill(buffers, null);
            for (Buffer buf : bufs) {
                CacheManager.getInstance().close(buf);
            }
        }
    }

    public void resetAccess() {
        if (access) {
            access = false;
        }
    }

    public boolean isAccess() {
        return access;
    }

    /**
     * 删除操作
     *
     * @return
     */
    public void delete() {

        synchronized (this) {
            close();
            boolean delete = connector.delete(new FileBlock(uri, FileConstants.HEAD));
            connector.delete(new FileBlock(uri));
        }
    }
}
