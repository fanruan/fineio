package com.fineio.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yee
 * @date 2018/4/15
 */
public class FineIOObservable implements Comparable<FineIOObservable> {
    protected CallBack callBack;

    private ExecutorService notify = Executors.newCachedThreadPool();

    public FineIOObservable(CallBack callBack) {
        this.callBack = callBack;
        this.observers = new ArrayList<FineIOObserver>();
    }

    private CountDownLatch latch = null;

    private List<FineIOObserver> observers;

    public void notifyAllChildren() {
        latch = new CountDownLatch(observers.size());
        for (FineIOObserver observer : observers) {
            this.notify.submit(new NotifyRunnable(observer));
        }
        this.notify.submit(new Runnable() {
            public void run() {
                try {
                    latch.await();
                    observers.clear();
                    callBack.call();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void registerObserver(FineIOObserver observer) {
        observers.add(observer);
    }

    public void stopService() {
        notifyAllChildren();
        this.notify.shutdown();
    }

    public int compareTo(FineIOObservable o) {
        return observers.size() - o.observers.size();
    }

    private class NotifyRunnable implements Runnable {
        private FineIOObserver observer;

        public NotifyRunnable(FineIOObserver observer) {
            this.observer = observer;
        }

        public void run() {
            observer.update(FineIOObservable.this);
            latch.countDown();
        }
    }
}
