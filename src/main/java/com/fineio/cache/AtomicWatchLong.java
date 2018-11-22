package com.fineio.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by daniel on 2017/3/3.
 */
public class AtomicWatchLong {
    private List<Watcher> listener = new ArrayList<Watcher>();

    private volatile AtomicLong value = new AtomicLong(0);

    public void addListener(Watcher watcher){
        listener.add(watcher);
    }

    public AtomicWatchLong() {
    }

    public AtomicWatchLong(Watcher listener) {
        this.listener.add(listener);
    }

    private void  triggerWatch(long v) {
        for(Watcher  w: listener) {
            w.watch(v);
        }
    }

    private long watchAndAdd(long v) {
        long r = value.addAndGet(v);
        triggerWatch(v);
        return r;
    }

    /**
     * 返回加之后结果
     * @param v
     * @return
     */
    public long add(long v) {
        return  v == 0 ? get() : watchAndAdd(v);
    }

    /**
     * 获取值
     * @return
     */
    public long  get(){
        return value.get();
    }

}
