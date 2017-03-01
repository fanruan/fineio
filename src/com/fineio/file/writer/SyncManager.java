package com.fineio.file.writer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by daniel on 2017/2/23.
 * 可控线程池，最多cpu + 1数量的线程，相同的等待任务将被合并
 */
public final class SyncManager {

    private static final int ThreadsCount = Runtime.getRuntime().availableProcessors() + 1;

    public static SyncManager instance = new SyncManager();

    private SyncManager() {
        watch_thread.start();
    }


    public static SyncManager getInstance() {
        return instance;
    }

    private volatile AtomicInteger working_jobs = new AtomicInteger(0);

    private ExecutorService executor = Executors.newFixedThreadPool(ThreadsCount);

    private volatile  JobContainer map = new JobContainer();

    private volatile Map<SyncKey, JobAssist> runningThread = new ConcurrentHashMap<SyncKey, JobAssist>();

    private Lock runningLock = new ReentrantLock();

    public  void triggerWork(JobAssist jobAssist) {
        if(map.put(jobAssist)) {
            wakeUpWatchTread();
        }
    }

    private void wakeUpWatchTread(){
        synchronized (watch_thread) {
            watch_thread.notify();
        }
    }

    public void force(JobAssist jobAssist) {
        runningLock.lock();
        JobAssist assist  =  runningThread.get(jobAssist.getKey());
        if(assist != null) {
            synchronized (assist){
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
        map.waitJob(jobAssist, new Job() {
            @Override
            public void doJob() {
                wakeUpWatchTread();
            }
        });
    }

    private Thread watch_thread = new Thread() {
        public void run() {
            while (true) {
                while (map.isEmpty() || working_jobs.get() > ThreadsCount) {
                    synchronized (this) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
                while (working_jobs.get() < ThreadsCount && !map.isEmpty()) {
                    final JobAssist jobAssist =  map.get();
                    if(jobAssist != null) {
                        //控制相同的任务不会同时执行，塞到屁股后面
                        if(runningThread.containsKey(jobAssist.getKey())) {
                            triggerWork(jobAssist);
                            continue;
                        }
                        runningLock.lock();
                        runningThread.put(jobAssist.getKey(), jobAssist);
                        runningLock.unlock();
                        working_jobs.addAndGet(1);
                        executor.execute(new Runnable() {
                            public void run() {
                                try {
                                    jobAssist.doJob();
                                } catch (Throwable e) {
                                } finally {
                                    runningLock.lock();
                                    JobAssist assist =  runningThread.remove(jobAssist.getKey());
                                    synchronized (assist) {
                                        assist.notifyAll();
                                    }
                                    runningLock.unlock();
                                    working_jobs.addAndGet(-1);
                                    synchronized (watch_thread) {
                                        watch_thread.notify();
                                    }
                                }
                            }
                        });

                    }
                }
            }
        }
    };





}
