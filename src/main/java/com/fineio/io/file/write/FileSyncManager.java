package com.fineio.io.file.write;

import com.fineio.FineIoService;
import com.fineio.io.Buffer;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.thread.FineIOExecutors;
import com.fineio.v21.cache.CacheManager;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author yee
 * @date 2019/9/11
 */
public class FileSyncManager implements FineIoService {
    private ExecutorService service;

    private FileSyncManager() {
    }

    public static FileSyncManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Future sync(Buffer buf) {
        return service.submit(new SyncTask(buf));
    }

    @Override
    public void start() {
        this.service = FineIOExecutors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), "FineIO-FileSync");
    }

    @Override
    public void stop() {
        if (!service.isShutdown()) {
            service.shutdown();
        }
    }

    private static class SingletonHolder {
        private static FileSyncManager INSTANCE = new FileSyncManager();
    }

    private static class SyncTask extends FutureTask {

        SyncTask(final Buffer buf) {
            super(new Callable() {
                @Override
                public Object call() throws Exception {
                    final Connector connector = buf.getBufferKey().getConnector();
                    final FileBlock block = buf.getBufferKey().getBlock();
                    final InputStream inputStream = buf.asInputStream();
                    if (inputStream.available() > 0) {
                        connector.write(block, inputStream);
                        buf.flip();
                        CacheManager.getInstance().put(buf);
                    }
                    return null;
                }
            });
        }
    }
}
