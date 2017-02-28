package com.fineio.file.writer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by daniel on 2017/2/23.
 * 可控线程池，最多cpu数量的线程，相同的等待任务将被合并
 */
public final class SyncManager {

    private static final int ThreadsCount = Runtime.getRuntime().availableProcessors();

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

    public  void triggerWork(JobAssist jobAssist) {
        if(map.put(jobAssist)) {
            synchronized (watch_thread) {
                watch_thread.notify();
            }
        }
    }

    public void force(JobAssist jobAssist) {
        synchronized (runningThread) {
            JobAssist assist =  runningThread.get(jobAssist.getKey());
            if(assist != null) {
                synchronized (assist){
                    try {
                        assist.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        map.waitJob(jobAssist);
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
                        synchronized (runningThread) {
                            runningThread.put(jobAssist.getKey(), jobAssist);
                        }
                        working_jobs.addAndGet(1);
                        executor.execute(new Runnable() {
                            public void run() {
                                try {
                                    jobAssist.doJob();
                                } catch (Throwable e) {
                                } finally {
                                    synchronized (runningThread){
                                        JobAssist assist =  runningThread.remove(jobAssist.getKey());
                                        synchronized (assist) {
                                            assist.notifyAll();
                                        }
                                    }
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
