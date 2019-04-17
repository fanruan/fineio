package com.fineio.v3.file.impl.write;

import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.file.impl.File;
import com.fineio.v3.file.sync.FileSync;
import com.fineio.v3.file.sync.FileSyncJob;
import com.fineio.v3.memory.Offset;

import java.util.Iterator;

/**
 * @author yee
 */
abstract class WriteFile<B extends DirectBuffer> extends File<B> {
    private int curBuf = -1;

    WriteFile(FileKey fileKey, Offset offset, Connector connector) {
        super(fileKey, offset, connector);
    }

    void delete() {
        connector.delete(fileKey);
    }

    void syncBufIfNeed(int nthBuf) {
        if (curBuf == -1) {
            curBuf = nthBuf;
        } else if (curBuf != nthBuf) {
            syncBuf(getBuffer(curBuf));
            buffers.remove(curBuf);

            curBuf = nthBuf;
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            for (Iterator<B> itr = buffers.values().iterator(); itr.hasNext(); ) {
                try {
                    syncBuf(itr.next());
                    itr.remove();
                } catch (Exception e) {
                    FineIOLoggers.getLogger().error(e);
                }
            }
        }
    }

    private void syncBuf(B buf) {
        FileSync.get().submit(new FileSyncJob(buf, connector));
    }
}