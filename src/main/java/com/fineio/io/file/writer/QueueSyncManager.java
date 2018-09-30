package com.fineio.io.file.writer;

import com.fineio.exception.IOSetException;
import com.fineio.io.base.BufferKey;
import com.fineio.io.base.JobAssist;
import com.fineio.io.file.writer.task.Pair;
import com.fineio.logger.FineIOLoggers;
import com.fineio.thread.FineIOExecutors;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yee
 * @date 2018/9/30
 */
public enum QueueSyncManager {
    /**
     * 队列实现
     */
    INSTANCE;
    private final int DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors() + 1;

    private volatile int threads = DEFAULT_THREAD_COUNT;
    private volatile AtomicInteger workingJobs = new AtomicInteger(0);
    private ExecutorService executor = FineIOExecutors.newFixedThreadPool(threads, SyncManager.class);
    private volatile LinkedBlockingQueue<JobAssist> jobs;
    private volatile Map<BufferKey, JobAssist> runningThread = new ConcurrentHashMap<BufferKey, JobAssist>();
    private Lock runningLock = new ReentrantLock();
    private Thread watchThread = new Thread("FineIO-SyncManager-Demon") {
        @Override
        public void run() {
            while (true) {

                try {
                    final JobAssist jobAssist = jobs.take();
                    while (workingJobs.get() < threads) {
                        if (jobAssist != null) {
                            //控制相同的任务不会同时执行，塞到屁股后面，这里可以不需要加锁，添加元素是单线程操作，remove是多线程的结果
                            //最多出现contains的情况那么会丢到任务末尾重新执行，如果已经被remove那么判断也没有问题
                            if (runningThread.containsKey(jobAssist.getKey())) {
                                triggerWork(jobAssist);
                                continue;
                            }
                            runningLock.lock();
                            runningThread.put(jobAssist.getKey(), jobAssist);
                            runningLock.unlock();
                            workingJobs.addAndGet(1);
                            Future<Pair<URI, Boolean>> future = executor.submit(new Callable<Pair<URI, Boolean>>() {
                                public Pair<URI, Boolean> call() {
                                    URI uri = jobAssist.getKey().getBlock().getBlockURI();
                                    Pair<URI, Boolean> pair = new Pair<URI, Boolean>(uri, true);
                                    try {
                                        jobAssist.doJob();
                                    } catch (Throwable e) {
                                        //TODO对与失败的处理//比如磁盘满啊 之类
                                        FineIOLoggers.getLogger().error(e);
                                        pair.setValue(false);
                                    } finally {
                                        runningLock.lock();
                                        JobAssist assist = runningThread.remove(jobAssist.getKey());
                                        synchronized (assist) {
                                            assist.notifyJobs();
                                        }
                                        runningLock.unlock();
                                        workingJobs.addAndGet(-1);
//                                        wakeUpWatchTread();
                                    }
                                    return pair;
                                }
                            });
                            try {
                                JobFinishedManager.getInstance().submit(future);
                            } catch (Exception e) {
                                FineIOLoggers.getLogger().error(e);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    QueueSyncManager() {
        jobs = new LinkedBlockingQueue<JobAssist>();
        watchThread.start();
    }

    /**
     * 获取写线程数量
     *
     * @return
     */
    public int getThreads() {
        return threads;
    }

    /**
     * 设置写线程数量
     *
     * @param threads
     */
    public void setThreads(int threads) {
        if (threads > 0) {
            this.threads = threads;
        } else {
            throw new IOSetException("thread counts must max than zero : " + threads);
        }
    }

    public void release() {
        synchronized (this) {
            executor.shutdown();
        }
    }

    public void triggerWork(JobAssist jobAssist) {
        jobs.offer(jobAssist);
    }

    private void wakeUpWatchTread() {
        synchronized (watchThread) {
            watchThread.notify();
        }
    }

    public void force(JobAssist jobAssist) {
        runningLock.lock();
        JobAssist assist = runningThread.get(jobAssist.getKey());
        if (assist != null) {
            synchronized (assist) {
                //这里使用lock而不是同步的目的是避免 assist中间被notify
                runningLock.unlock();
                try {
                    assist.wait();
                } catch (InterruptedException e) {
                }
            }
            return;
        } else {
            runningLock.unlock();
        }
        jobs.offer(jobAssist);
    }

}
