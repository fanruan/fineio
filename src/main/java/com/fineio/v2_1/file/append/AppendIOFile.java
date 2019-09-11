package com.fineio.v2_1.file.append;

import com.fineio.io.base.BufferKey;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v2_1.file.read.ReadIOFile;
import com.fineio.v2_1.file.write.WriteIOFile;
import com.fineio.v2_1.unsafe.ByteUnsafeBuf;
import com.fineio.v2_1.unsafe.UnsafeBuf;
import com.fineio.v2_1.unsafe.impl.BaseUnsafeBuf;

import java.io.IOException;
import java.net.URI;

/**
 * @author yee
 * @date 2019/9/11
 */
public class AppendIOFile<B extends UnsafeBuf> extends ReadIOFile<B> {
    private WriteIOFile<B> writeFile;
    private int lastPos;

    protected AppendIOFile(Connector connector, URI uri, byte offset) {
        super(connector, uri, offset);
        ByteUnsafeBuf byteUnsafeBuf = BaseUnsafeBuf.newBuffer(new BufferKey(connector, new FileBlock(uri, String.valueOf(buffers.length - 1))));
        long l = byteUnsafeBuf.getMemorySize() >> offset;
        this.writeFile = new WriteIOFile<B>(connector, uri, offset);
    }


    @Override
    public void close() throws IOException {
        this.writeFile.close();
    }
}
