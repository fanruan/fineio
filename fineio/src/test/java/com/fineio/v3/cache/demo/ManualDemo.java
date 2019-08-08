package com.fineio.v3.cache.demo;

import com.fineio.v3.cache.core.Cache;
import com.fineio.v3.cache.core.Caffeine;
import com.fineio.v3.cache.core.stats.CacheStats;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2019/5/21
 *
 * @author Lucifer
 * @description
 */
public class ManualDemo extends BaseDemo {

    @Test
    public void testManual() {

        Cache<String, String> manualCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10_000).recordStats()
                .build();

        ConcurrentMap<String, String> asMap = manualCache.asMap();

        String key = "key";
        String graph = manualCache.getIfPresent(key);
        Assert.assertNull(graph);
        Assert.assertTrue(asMap.isEmpty());

        graph = manualCache.get(key, k -> createExpensiveGraph(k));
        Assert.assertEquals(graph, key + key);
        Assert.assertEquals(asMap.size(), 1);

        manualCache.put(key, graph + key);
        Assert.assertEquals(manualCache.getIfPresent(key), key + key + key);
        Assert.assertEquals(asMap.get(key), key + key + key);

        manualCache.invalidate(key);
        graph = manualCache.getIfPresent(key);
        Assert.assertNull(graph);
        Assert.assertTrue(asMap.isEmpty());

        CacheStats cacheStats = manualCache.stats();
        Assert.assertEquals(cacheStats.hitCount(), 1);
        Assert.assertEquals(cacheStats.missCount(), 3);
        Assert.assertEquals(cacheStats.loadSuccessCount(), 1);
        Assert.assertEquals(cacheStats.loadFailureCount(), 0);
    }

}
