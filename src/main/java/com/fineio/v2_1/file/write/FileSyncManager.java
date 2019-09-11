package com.fineio.v2_1.file.write;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.thread.FineIOExecutors;
import com.fineio.v2_1.unsafe.UnsafeBuf;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author yee
 * @date 2019/9/11
 */
public class FileSyncManager {
    private ExecutorService service;

    private FileSyncManager() {
        this.service = FineIOExecutors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), "FineIO-FileSync");
    }

    public static FileSyncManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Future sync(UnsafeBuf buf) {
        return service.submit(new SyncTask(buf));
    }

    private static class SingletonHolder {
        private static FileSyncManager INSTANCE = new FileSyncManager();
    }

    private static class SyncTask extends FutureTask {

        SyncTask(final UnsafeBuf buf) {
            super(new Callable() {
                @Override
                public Object call() throws Exception {
                    final Connector connector = buf.getBufferKey().getConnector();
                    final FileBlock block = buf.getBufferKey().getBlock();
                    final InputStream inputStream = buf.asInputStream();
                    if (inputStream.available() > 0) {
                        connector.write(block, inputStream);
                        buf.close();
                        // TODO 这边将Buf加入Cache
                    }
                    return null;
                }
            });
        }
    }
}
