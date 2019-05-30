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
import sun.reflect.CallerSensitive;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yee
 */
public abstract class WriteFile<B extends DirectBuffer> extends File<B> implements IWriteFile<B> {
    final ConcurrentMap<Integer, B> buffers = new ConcurrentHashMap<>();

    private int curBuf = -1;

    private final boolean asyncWrite;

    WriteFile(FileBlock fileBlock, Offset offset, Connector connector, boolean asyncWrite) {
        super(fileBlock, offset, connector);
        this.asyncWrite = asyncWrite;
    }

    void delete() {
        connector.delete(fileBlock);
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

    /**
     * 给append file调的
     * 禁止给别的类用
     *
     * @param nthBuf 第n个buf
     * @param buf    buf
     */
    @CallerSensitive
    public void putBuffer(int nthBuf, B buf) {
        buffers.putIfAbsent(nthBuf, buf);
    }
}