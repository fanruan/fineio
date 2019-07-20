package com.fineio.v3.file.impl.write;

import com.fineio.accessor.file.IWriteFile;
import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.impl.File;
import com.fineio.v3.file.sync.FileSync;
import com.fineio.v3.file.sync.FileSyncJob;
import com.fineio.v3.memory.Offset;

import java.util.Arrays;

/**
 * @author yee
 */
public abstract class WriteFile<B extends DirectBuffer> extends File<B> implements IWriteFile<B> {
    private int curBuf = -1;

    private final boolean asyncWrite;

    private int lastPos;

    WriteFile(FileBlock fileBlock, Offset offset, Connector connector, boolean asyncWrite) {
        super(fileBlock, offset, connector);
        this.asyncWrite = asyncWrite;
    }

    void growBufferCache(int nthBuf) {
        if (nthBuf >= buffers.length) {
            buffers = Arrays.copyOf(buffers, nthBuf + 16);
        }
    }

    void updateLastPos(int pos) {
        if (pos >= lastPos) {
            lastPos = pos + 1;
        }
    }

    void syncBufIfNeed(int nthBuf) {
        if (curBuf == -1) {
            curBuf = nthBuf;
        } else if (curBuf != nthBuf && nthBuf >= 0) {
            syncBuf(buffers[curBuf]);
            buffers[curBuf] = null;

            curBuf = nthBuf;
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            syncBufs();
            writeLastPos(this, lastPos);
        }
    }

    private void syncBufs() {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i] != null) {
                try {
                    syncBuf(buffers[i]);
                    buffers[i] = null;
                } catch (Exception e) {
                    FineIOLoggers.getLogger().error(e);
                }
            }
        }
    }

    private void syncBuf(B buf) {
        FileSyncJob job = new FileSyncJob(buf, connector);
        if (asyncWrite) {
            FileSync.get().submit(job);
        } else {
            try {
                job.run();
            } catch (Exception e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
    }

    void delete() {
        connector.delete(fileBlock);
    }
}