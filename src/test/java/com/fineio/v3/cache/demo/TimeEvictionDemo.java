package com.fineio.v3.cache.demo;

import com.fineio.v3.cache.core.Cache;
import com.fineio.v3.cache.core.Caffeine;
import com.fineio.v3.cache.core.Expiry;
import com.fineio.v3.cache.core.LoadingCache;
import com.google.common.testing.FakeTicker;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2019/5/21
 *
 * @author Lucifer
 * @description
 */
public class TimeEvictionDemo extends BaseDemo {

    @Test
    public void testTimeEvictionAfterAccess() throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        LoadingCache<String, String> cache1 = Caffeine.newBuilder()
                .expireAfterAccess(3, TimeUnit.SECONDS)
                .build(key -> createExpensiveGraph(key));

        String key1 = "key1";
        Assert.assertEquals(cache1.get(key1), key1 + key1);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (count < 6) {
                    Assert.assertEquals(cache1.get(key1), key1 + key1);
                    try {
                        Thread.sleep(500L);
                        count++;
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        Thread.sleep(3000L);
        Assert.assertTrue(cache1.asMap().containsKey(key1));
        Thread.sleep(3000L);
        Assert.assertTrue(!cache1.asMap().containsKey(key1));
    }

    @Test
    public void testTimeEvictionAfterWrite() throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        LoadingCache<String, String> cache1 = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .build(key -> createExpensiveGraph(key));


        String key1 = "key1";
        Assert.assertEquals(cache1.get(key1), key1 + key1);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (count < 6) {
                    Assert.assertEquals(cache1.get(key1), key1 + key1);
                    try {
                        Thread.sleep(500L);
                        count++;
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        Thread.sleep(3000L);
        Assert.assertTrue(!cache1.asMap().containsKey(key1));
    }

    @Test
    public void testTimeEvictionAfter() throws InterruptedException {
        LoadingCache<String, String> cache1 = Caffeine.newBuilder()
                .expireAfter(new Expiry<String, String>() {
                    @Override
                    public long expireAfterCreate(String key, String graph, long currentTime) {
                        return TimeUnit.SECONDS.toNanos(3);
                    }

                    @Override
                    public long expireAfterUpdate(String key, String graph,
                                                  long currentTime, long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(String key, String graph,
                                                long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .build(key -> createExpensiveGraph(key));

        String key1 = "key1";
        Assert.assertEquals(cache1.get(key1), key1 + key1);
        Assert.assertTrue(cache1.asMap().containsKey(key1));
        Thread.sleep(5000L);
        Assert.assertTrue(!cache1.asMap().containsKey(key1));
    }

    @Test
    public void testTimeEvictionWithTicker() {
        FakeTicker ticker = new FakeTicker();
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .executor(Runnable::run)
                .ticker(ticker::read)
                .maximumSize(10)
                .build();
    }
}
