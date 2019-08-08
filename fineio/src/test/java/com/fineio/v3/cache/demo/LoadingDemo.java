package com.fineio.v3.cache.demo;

import com.fineio.v3.cache.core.Caffeine;
import com.fineio.v3.cache.core.LoadingCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2019/5/21
 *
 * @author Lucifer
 * @description
 */
public class LoadingDemo extends BaseDemo {

    @Test
    public void testLoadin() {
        LoadingCache<String, String> loadingCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(key -> createExpensiveGraph(key));

        String key1 = "key1";
        String graph = loadingCache.get(key1);
        Assert.assertEquals(graph, key1 + key1);

        String key2 = "key2";
        List<String> keys = new ArrayList<>();
        keys.add(key2);
        Map<String, String> graphs = loadingCache.getAll(keys);
        Assert.assertEquals(graphs.get(key2), key2 + key2);
    }

}
