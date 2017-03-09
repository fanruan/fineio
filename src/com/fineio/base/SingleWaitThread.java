package com.fineio.base;

/**
 * Created by daniel on 2017/3/6.
 */
public class SingleWaitThread extends Thread {
    private volatile boolean waiter = false;
    private volatile boolean stop = false;

    private Worker worker;

    public SingleWaitThread(Worker worker) {
        this.worker = worker;
        this.start();
    }

    public void run() {
        while (!stop){
            while (!waiter){
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            this.waiter = false;
            try {
                if(this.worker != null) {
                    this.worker.work();
                }
            } catch (Throwable e) {
            } finally {
            }
        }
    }


    public void triggerWork() {
        if(!this.waiter) {
            this.waiter = true;
            synchronized (this) {
                this.notify();
            }
        }
    }


    public void clear () {
        stop = true;
        waiter = true;
        this.interrupt();
    }


}
