package com.fineio.v2_1.file.read;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.io.base.BufferKey;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileConstants;
import com.fineio.storage.Connector;
import com.fineio.v2_1.file.IOFile;
import com.fineio.v2_1.unsafe.ByteUnsafeBuf;
import com.fineio.v2_1.unsafe.DoubleUnsafeBuf;
import com.fineio.v2_1.unsafe.IntUnsafeBuf;
import com.fineio.v2_1.unsafe.LongUnsafeBuf;
import com.fineio.v2_1.unsafe.UnsafeBuf;
import com.fineio.v2_1.unsafe.impl.BaseUnsafeBuf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2019/9/11
 */
public class ReadIOFile<B extends UnsafeBuf> extends IOFile<B> {

    protected ReadIOFile(Connector connector, URI uri, byte offset) {
        super(connector, uri);
        readHeader(offset);
    }

    private void readHeader(byte offset) {
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

    private void initBufferArray(byte offset, byte[] header) {
        int p = 0;
        createBufferArray(Bits.getInt(header, p));
        //先空个long的位置
        p += IOFile.STEP_LEN;
        blockSizeOffset = (byte) (header[p] - offset);
    }

    public int getInt(int pos) {
        try {
            return ((IntUnsafeBuf) getBuffer(pos)).getInt((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final IntUnsafeBuf byteUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(i)))).asInt();
            buffers[i] = byteUnsafeBuf;
            return byteUnsafeBuf.getInt((int) (pos & singleBlockLen));
        }
    }

    public byte getByte(int pos) {
        try {
            return ((ByteUnsafeBuf) getBuffer(pos)).getByte((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final ByteUnsafeBuf byteUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(i))));
            buffers[i] = byteUnsafeBuf;
            return byteUnsafeBuf.getByte((int) (pos & singleBlockLen));
        }
    }

    public long getLong(int pos) {
        try {
            return ((LongUnsafeBuf) getBuffer(pos)).getLong((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final LongUnsafeBuf byteUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(i)))).asLong();
            buffers[i] = byteUnsafeBuf;
            return byteUnsafeBuf.getLong((int) (pos & singleBlockLen));
        }
    }

    public double getDouble(int pos) {
        try {
            return ((DoubleUnsafeBuf) getBuffer(pos)).getDouble((int) (pos & singleBlockLen));
        } catch (NullPointerException e) {
            final int i = pos >> blockSizeOffset;
            final DoubleUnsafeBuf byteUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(i)))).asDouble();
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
