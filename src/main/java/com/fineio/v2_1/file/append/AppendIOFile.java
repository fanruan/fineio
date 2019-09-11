package com.fineio.v2_1.file.append;

import com.fineio.storage.Connector;
import com.fineio.v2_1.file.IOFile;
import com.fineio.v2_1.unsafe.UnsafeBuf;

import java.io.IOException;
import java.net.URI;

/**
 * @author yee
 * @date 2019/9/11
 */
public class AppendIOFile<B extends UnsafeBuf> extends IOFile<B> {

    protected AppendIOFile(Connector connector, URI uri) {
        super(connector, uri);
    }


    @Override
    public void close() throws IOException {

    }
}
