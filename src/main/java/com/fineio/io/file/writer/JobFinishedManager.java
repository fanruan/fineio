package com.fineio.io.file.writer;

import com.fineio.io.base.Job;
import com.fineio.io.file.writer.task.DoneTaskKey;
import com.fineio.io.file.writer.task.FinishOneTaskKey;
import com.fineio.io.file.writer.task.Pair;
import com.fineio.io.file.writer.task.TaskKey;
import com.fineio.logger.FineIOLoggers;
import com.fineio.thread.FineIOExecutors;

import java.net.URI;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/7/12
 */
public final class JobFinishedManager {
    private ExecutorCompletionService<Job> service =
            new ExecutorCompletionService<Job>(FineIOExecutors.newSingleThreadExecutor("JobFinishedManager-complete"));
    private ExecutorService consume = FineIOExecutors.newSingleThreadExecutor(JobFinishedManager.class);
    private final Queue<Pair<TaskKey, Object>> queue = new ConcurrentLinkedQueue<Pair<TaskKey, Object>>();
    private volatile static JobFinishedManager instance;
    private ScheduledExecutorService timer = FineIOExecutors.newScheduledExecutorService(1, "JobFinishedManager-timer");


    public static JobFinishedManager getInstance() {
        if (instance == null) {
            synchronized (SyncManager.class) {
                if (instance == null) {
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
                        service.take().get().doJob();
                        synchronized (queue) {
                            Pair<TaskKey, Object> pair = null;
                            while ((pair = queue.peek()) != null) {
                                if (pair.getKey().getType().equals(TaskKey.KeyType.DONE)) {
                                    queue.poll();
                                    ((Runnable) pair.getValue()).run();
                                    FineIOLoggers.getLogger().debug("Run finish Task");
                                } else {
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        FineIOLoggers.getLogger().error(e);
                    }
                }
            }
        });
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!queue.isEmpty()) {
                    submit(URI.create("JobFinishedManager_timer_checker"));
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    void submit(final URI uri) {
        service.submit(new Callable<Job>() {
            @Override
            public Job call() {
                return new Job() {
                    @Override
                    public void doJob() {
                        synchronized (queue) {
                            Iterator<Pair<TaskKey, Object>> iterator = queue.iterator();
                            while (iterator.hasNext()) {
                                Pair<TaskKey, Object> pair = iterator.next();
                                if (pair.getKey().getType().equals(TaskKey.KeyType.DONE)) {
                                    iterator.remove();
                                    ((Runnable) pair.getValue()).run();
                                    FineIOLoggers.getLogger().debug("Run finish Task");
                                } else if (uri.equals(pair.getValue())) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                    }
                };
            }
        });
    }

    public void addTask(URI uri) {
        synchronized (queue) {
            queue.offer(new Pair<TaskKey, Object>(new FinishOneTaskKey(uri), uri));
        }
    }

    public <T> Future<T> finish(Callable<T> runnable) {
        synchronized (queue) {
            FutureTask<T> task = new FutureTask<T>(runnable);
            queue.offer(new Pair<TaskKey, Object>(new DoneTaskKey(), task));
            return task;
        }
    }

    public Future<Void> finish(Runnable runnable) {
        synchronized (queue) {
            FutureTask<Void> task = new FutureTask<Void>(runnable, null);
            queue.offer(new Pair<TaskKey, Object>(new DoneTaskKey(), task));
            return task;
        }
    }

}
