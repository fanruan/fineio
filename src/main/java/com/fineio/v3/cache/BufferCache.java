package com.fineio.v3.cache;

import com.fineio.v3.cache.core.CacheLoader;
import com.fineio.v3.cache.core.Caffeine;
import com.fineio.v3.cache.core.LoadingCache;
import com.fineio.v3.cache.core.RemovalListener;

import java.util.concurrent.TimeUnit;

/**
 * This class created on 2019/5/21
 *
 * @author Lucifer
 * @description
 */
public class BufferCache<K, V> {

    private static BufferCache ourInstance = new BufferCache();

    public static BufferCache getInstance() {
        return ourInstance;
    }

    private LoadingCache<K, V> cache;

    private BufferCache() {
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    void bulid(long size, CacheLoader loader, RemovalListener removalListener) {
        Caffeine caffeine = Caffeine.newBuilder();
        caffeine.maximumSize(size);
        caffeine.expireAfterAccess(60, TimeUnit.MINUTES);
        caffeine.recordStats();
        if (removalListener != null) {
            caffeine.removalListener(removalListener);
        }
        cache = caffeine.build(loader);
    }
}
