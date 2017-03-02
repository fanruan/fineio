package com.fineio.cache;

/**
 * Created by daniel on 2017/3/1.
 */
public class CacheObject<T> {

    private long t = System.currentTimeMillis();

    private T value;

    public void updateTime(){
        t = System.currentTimeMillis();
    }


}
