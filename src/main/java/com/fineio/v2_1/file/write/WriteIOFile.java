package com.fineio.v2_1.file.write;

import com.fineio.base.Bits;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileConstants;
import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;
import com.fineio.v2_1.file.IOFile;
import com.fineio.v2_1.unsafe.UnsafeBuf;

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

    protected WriteIOFile(Connector connector, URI uri, int offset) {
        super(connector, uri);
        this.offset = offset;
    }

    @Override
    public void close() throws IOException {
        if (close.compareAndSet(false, true)) {
            writeHead();
            List<Future> futures = new ArrayList<Future>(buffers.length);
            for (UnsafeBuf buffer : buffers) {
                futures.add(FileSyncManager.getInstance().sync(buffer));
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
}
