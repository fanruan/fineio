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
    WriteFile(FileKey fileKey, Offset offset, Connector connector) {
        super(fileKey, offset, connector);
    }

    void delete() {
        connector.delete(fileKey);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            for (Iterator<B> itr = buffers.values().iterator(); itr.hasNext(); ) {
                try {
                    FileSync.get().submit(new FileSyncJob(itr.next(), connector));
                    itr.remove();
                } catch (Exception e) {
                    FineIOLoggers.getLogger().error(e);
                }
            }
        }
    }
}