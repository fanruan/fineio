package com.fineio.cache;

/**
 * Created by daniel on 2017/3/1.
 */
public class CacheObject<T> {

    private long t = System.currentTimeMillis();

    private LEVEL level;

    private T value;

    public void updateTime(){
        t = System.currentTimeMillis();
    }

    public CacheObject(T value, LEVEL level){
        this.value = value;
        this.level = level;
    }

    public T get() {
        return value;
    }

    public long getIdle() {
        return System.currentTimeMillis() - t;
    }

    public LEVEL getLevel(){
        return level;
    }


}
