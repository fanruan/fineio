package com.fineio.v21.file.read;

import com.fineio.exception.BlockNotFoundException;
import com.fineio.io.base.BufferKey;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v21.cache.CacheManager;
import com.fineio.v21.file.IOFile;
import com.fineio.v21.unsafe.ByteUnsafeBuf;
import com.fineio.v21.unsafe.DoubleUnsafeBuf;
import com.fineio.v21.unsafe.IntUnsafeBuf;
import com.fineio.v21.unsafe.LongUnsafeBuf;
import com.fineio.v21.unsafe.UnsafeBuf;
import com.fineio.v21.unsafe.impl.BaseUnsafeBuf;

import java.io.IOException;
import java.net.URI;

/**
 * @author yee
 * @date 2019/9/11
 */
public class ReadIOFile<B extends UnsafeBuf> extends IOFile<B> {

    protected ReadIOFile(Connector connector, URI uri, int offset) {
        super(connector, uri);
        readHeader(offset);
    }

    public static <B extends UnsafeBuf> ReadIOFile<B> createFile(Connector connector, URI uri, int offset) {
        return new ReadIOFile<B>(connector, uri, offset);
    }

    public int getInt(int pos) {
        try {
            return ((IntUnsafeBuf) getBuffer(pos)).getInt((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final FileBlock block = new FileBlock(uri, String.valueOf(i));
            final BufferKey bufferKey = new BufferKey(connector, block);
            buffers[i] = CacheManager.getInstance().get(bufferKey.getBlock().getBlockURI(), new CacheManager.BufferCreator() {
                @Override
                public UnsafeBuf createBuffer() {
                    return BaseUnsafeBuf.newBuffer(bufferKey).asInt();
                }
            });
            return ((IntUnsafeBuf) buffers[i]).getInt((int) (pos & singleBlockLen));
        }
    }

    public byte getByte(int pos) {
        try {
            return ((ByteUnsafeBuf) getBuffer(pos)).getByte((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final ByteUnsafeBuf byteUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(i))));
            CacheManager.getInstance().put(byteUnsafeBuf);
            buffers[i] = byteUnsafeBuf;
            return byteUnsafeBuf.getByte((int) (pos & singleBlockLen));
        }
    }

    public long getLong(int pos) {
        try {
            return ((LongUnsafeBuf) getBuffer(pos)).getLong((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final FileBlock block = new FileBlock(uri, String.valueOf(i));
            final BufferKey bufferKey = new BufferKey(connector, block);

            buffers[i] = CacheManager.getInstance().get(bufferKey.getBlock().getBlockURI(), new CacheManager.BufferCreator() {
                @Override
                public UnsafeBuf createBuffer() {
                    return BaseUnsafeBuf.newBuffer(bufferKey).asLong();
                }
            });
            return ((LongUnsafeBuf) buffers[i]).getLong((int) (pos & singleBlockLen));
        }
    }

    public double getDouble(int pos) {
        try {
            return ((DoubleUnsafeBuf) getBuffer(pos)).getDouble((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final DoubleUnsafeBuf byteUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(i)))).asDouble();
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
    public void close() throws IOException {
        if (close.compareAndSet(false, true)) {
            for (UnsafeBuf buffer : buffers) {
                if (null != buffer) {
                    buffer.close();
                }
            }
        }
    }
}
