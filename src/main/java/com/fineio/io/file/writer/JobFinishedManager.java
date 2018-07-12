package com.fineio.io.file.writer;

import com.fineio.io.file.writer.task.JobFutureTask;
import com.fineio.io.file.writer.task.Pair;
import com.fineio.io.file.writer.task.PreTaskFinishTask;
import com.fineio.io.file.writer.task.Task;
import com.fineio.logger.FineIOLoggers;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author yee
 * @date 2018/7/12
 */
public final class JobFinishedManager {
    private ExecutorCompletionService<Task> service =
            new ExecutorCompletionService(Executors.newSingleThreadExecutor());
    private ExecutorService consume = Executors.newSingleThreadExecutor();
    private volatile static JobFinishedManager instance;

    public static JobFinishedManager getInstance() {
        if(instance == null) {
            synchronized (SyncManager.class) {
                if(instance == null){
                    instance = new JobFinishedManager();
                }
            }
        }
        return instance;
    }

    private JobFinishedManager() {
        consume.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Future<Task> future = service.take();
                        future.get().run();
                    } catch (Exception e) {
                        FineIOLoggers.getLogger().error(e);
                    }
                }
            }
        });
    }

    void submit(final Future<Pair<URI, Boolean>> future) {
        service.submit(new Callable<Task>() {
            @Override
            public Task call() throws Exception {
                return new JobFutureTask(future);
            }
        });
    }

    public void finish(final Runnable runnable) {
        service.submit(new Callable<Task>() {
            @Override
            public Task call() throws Exception {
                return new PreTaskFinishTask(runnable);
            }
        });
    }
}
