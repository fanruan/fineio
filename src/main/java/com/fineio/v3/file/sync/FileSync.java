package com.fineio.v3.file.sync;

import com.fineio.logger.FineIOLoggers;
import com.fineio.thread.FineIOExecutors;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author anchore
 * @date 2019/4/2
 */
public class FileSync implements Runnable {

    private ExecutorService exec;

    private BlockingQueue<FileSyncJob> waitingJobs;

    private final Set<FileSyncJob> runningJobs;

    @Override
    public void run() {
        while (true) {
            try {
                FileSyncJob job = waitingJobs.take();
                synchronized (runningJobs) {
                    if (runningJobs.contains(job)) {
                        // TODO: 2019/4/2 anchore 相同任务取最后一个任务执行？，没必要多次写，反正都是覆盖
                        waitingJobs.put(job);
                    } else {
                        exec.execute(new NotifiableJob(job));
                        runningJobs.add(job);
                    }
                }
            } catch (Exception e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
    }

    public void submit(FileSyncJob job) {
        try {
            waitingJobs.put(job);
        } catch (InterruptedException e) {
            FineIOLoggers.getLogger().error(e);
        }
    }

    class NotifiableJob implements Runnable {
        FileSyncJob job;

        NotifiableJob(FileSyncJob job) {
            this.job = job;
        }

        @Override
        public void run() {
            try {
                job.run();
            } finally {
                synchronized (runningJobs) {
                    runningJobs.remove(job);
                }
            }
        }
    }

    private static final FileSync INSTANCE = new FileSync();

    static {
        new Thread(INSTANCE, INSTANCE.getClass().getSimpleName()).start();
    }

    private FileSync() {
        int threads = Runtime.getRuntime().availableProcessors();
        exec = FineIOExecutors.newFixedThreadPool(threads, FileSync.class.getSimpleName() + "-Worker");
        waitingJobs = new LinkedBlockingQueue<>();
        runningJobs = new HashSet<>();
    }

    public static FileSync get() {
        return INSTANCE;
    }
}