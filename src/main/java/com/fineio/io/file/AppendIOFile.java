package com.fineio.io.file;

import com.fineio.exception.BufferConstructException;
import com.fineio.io.Buffer;
import com.fineio.io.file.append.ByteAppendIOFile;
import com.fineio.io.file.append.DoubleAppendIOFile;
import com.fineio.io.file.append.IntAppendIOFile;
import com.fineio.io.file.append.LongAppendIOFile;
import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2019/9/11
 */
public abstract class AppendIOFile<B extends Buffer> extends WriteIOFile<B> {
    protected int lastPos;

    protected AppendIOFile(Connector connector, URI uri, byte offset) {
        super(connector, uri, offset);
        readHeader(offset);
        initLastBuffer(offset);
    }

    public static ByteAppendIOFile asByte(Connector connector, URI uri) {
        return new ByteAppendIOFile(connector, uri);
    }

    public static IntAppendIOFile asInt(Connector connector, URI uri) {
        return new IntAppendIOFile(connector, uri);
    }

    public static LongAppendIOFile asLong(Connector connector, URI uri) {
        return new LongAppendIOFile(connector, uri);
    }

    public static DoubleAppendIOFile asDouble(Connector connector, URI uri) {
        return new DoubleAppendIOFile(connector, uri);
    }

    private void initLastBuffer(int offset) {
        final int idx = buffers.length - 1;
        if (idx >= 0) {
            try {
                Buffer byteBuffer = createBuf(idx);
                buffers[idx] = byteBuffer.flip();
                lastPos = (int) (byteBuffer.getMemorySize() + (idx << connector.getBlockOffset())) >> offset;
            } catch (BufferConstructException e) {
                FineIOLoggers.getLogger().error(String.format("load buffer %s%d failed " + uri.getPath(), idx));
            }
        }
    }

    protected abstract Buffer createBuf(int idx);
}
