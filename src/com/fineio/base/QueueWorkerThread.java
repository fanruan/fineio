package com.fineio.base;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by danel on 2017/8/14.
 */
public class QueueWorkerThread extends Thread {
    private volatile AtomicInteger jobs = new AtomicInteger(0);
    private volatile boolean stop = false;

    private Worker worker;

    public QueueWorkerThread(Worker worker) {
        this.worker = worker;
        this.start();
    }

    public void run() {
        while (!stop) {
            if(jobs.intValue() == 0) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            while (jobs.intValue() != 0) {
                try {
                    if(this.worker != null) {
                        this.worker.work();
                    }
                } catch (Throwable e) {
                } finally {
                    jobs.addAndGet(-1);
                }
            }
        }
    }


    public void triggerWork() {
        jobs.addAndGet(1);
        synchronized (this) {
            this.notify();
        }
    }


    public void clear () {
        stop = true;
        jobs.set(0);
        this.interrupt();
    }

}
