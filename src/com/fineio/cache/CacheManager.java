package com.fineio.cache;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by daniel on 2017/3/1.
 */
public class CacheManager {

    private volatile static CacheManager instance;

    public static CacheManager getInstance(){
        if(instance == null){
            synchronized (CacheManager.class){
                if(instance == null){
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }

    private volatile AtomicLong memory_size;



}
