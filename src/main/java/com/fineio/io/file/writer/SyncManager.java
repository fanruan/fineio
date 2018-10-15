package com.fineio.io.file.writer;

import com.fineio.exception.IOSetException;
import com.fineio.io.base.BufferKey;
import com.fineio.io.base.Job;
import com.fineio.io.base.JobAssist;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class SyncManager {
    private static final int DEFAULT_THREAD_COUNT;
    public static volatile SyncManager instance;

    static {
        DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors() + 1;
    }

    private volatile int threads;
    private volatile AtomicInteger working_jobs;
    private ExecutorService executor;
    private volatile JobContainer map;
    private volatile Map<BufferKey, JobAssist> runningThread;
    private Lock runningLock;
    private Thread watch_thread;
    
    private SyncManager() {
        this.threads = SyncManager.DEFAULT_THREAD_COUNT;
        this.working_jobs = new AtomicInteger(0);
        this.executor = Executors.newCachedThreadPool();
        this.map = new JobContainer();
        this.runningThread = new ConcurrentHashMap<BufferKey, JobAssist>();
        this.runningLock = new ReentrantLock();
        (this.watch_thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (this.isWait()) {
                        synchronized (this) {
                            if (!this.isWait()) {
                                continue;
                            }
                            try {
                                this.wait();
                            } catch (InterruptedException ex) {
                            }
                        }
                    } else {
                        while (SyncManager.this.working_jobs.get() < SyncManager.this.threads && !SyncManager.this.map.isEmpty()) {
                            final JobAssist value = SyncManager.this.map.get();
                            if (value != null) {
                                if (SyncManager.this.runningThread.containsKey(value.getKey())) {
                                    SyncManager.this.triggerWork(value);
                                } else {
                                    SyncManager.this.runningLock.lock();
                                    SyncManager.this.runningThread.put(value.getKey(), value);
                                    SyncManager.this.runningLock.unlock();
                                    SyncManager.this.working_jobs.addAndGet(1);
                                    SyncManager.this.executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                value.doJob();
                                            } catch (Throwable t) {
                                                t.printStackTrace();
                                            } finally {
                                                SyncManager.this.runningLock.lock();
                                                final JobAssist jobAssist = SyncManager.this.runningThread.remove(value.getKey());
                                                synchronized (jobAssist) {
                                                    jobAssist.notifyJobs();
                                                }
                                                SyncManager.this.runningLock.unlock();
                                                SyncManager.this.working_jobs.addAndGet(-1);
                                                SyncManager.this.wakeUpWatchTread();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }

            private boolean isWait() {
                return SyncManager.this.map.isEmpty() || SyncManager.this.working_jobs.get() > SyncManager.this.threads;
            }
        }).start();
    }

    public static SyncManager getInstance() {
        if (SyncManager.instance == null) {
            synchronized (SyncManager.class) {
                if (SyncManager.instance == null) {
                    SyncManager.instance = new SyncManager();
                }
            }
        }
        return SyncManager.instance;
    }

    public static void release() {
        if (SyncManager.instance != null) {
            synchronized (SyncManager.class) {
                if (SyncManager.instance != null) {
                    SyncManager.instance.executor.shutdown();
                    SyncManager.instance = null;
                }
            }
        }
    }

    public int getThreads() {
        return this.threads;
    }

    public void setThreads(final int threads) {
        if (threads > 0) {
            this.threads = threads;
            return;
        }
        throw new IOSetException("thread counts must max than zero : " + threads);
    }

    public void triggerWork(final JobAssist jobAssist) {
        if (this.map.put(jobAssist)) {
            this.wakeUpWatchTread();
        }
    }

    private void wakeUpWatchTread() {
        synchronized (this.watch_thread) {
            this.watch_thread.notify();
        }
    }

    public void force(final JobAssist jobAssist) {
        this.runningLock.lock();
        final JobAssist jobAssist2 = this.runningThread.get(jobAssist.getKey());
        if (jobAssist2 != null) {
            synchronized (jobAssist2) {
                this.runningLock.unlock();
                try {
                    jobAssist2.wait();
                } catch (InterruptedException ex) {
                }
            }
            return;
        }
        this.runningLock.unlock();
        this.map.waitJob(jobAssist, new Job() {
            @Override
            public void doJob() {
                SyncManager.this.wakeUpWatchTread();
            }
        });
    }
}
