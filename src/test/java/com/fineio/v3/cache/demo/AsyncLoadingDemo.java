package com.fineio.v3.cache.demo;

import com.fineio.v3.cache.core.AsyncLoadingCache;
import com.fineio.v3.cache.core.Caffeine;
import com.fineio.v3.cache.core.LoadingCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2019/5/21
 *
 * @author Lucifer
 * @description
 */
public class AsyncLoadingDemo extends BaseDemo {

    @Test
    public void testAsyncLoading() throws ExecutionException, InterruptedException {
        AsyncLoadingCache<String, Object> asyncLoadingCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .buildAsync(key -> createExpensiveGraph(key));

        String key1 = "key1";

        CompletableFuture<Object> graph = asyncLoadingCache.get(key1);
        Assert.assertEquals(graph.get(), key1 + key1);

        String key2 = "key2";
        List<String> keys = new ArrayList<>();
        keys.add(key2);
        CompletableFuture<Map<String, Object>> graphs = asyncLoadingCache.getAll(keys);
        for (Map.Entry<String, Object> entry : graphs.get().entrySet()) {
            Assert.assertEquals(entry.getKey(), key2);
            Assert.assertEquals(entry.getValue(), key2 + key2);
        }

        //获得asyncLoading的一个loading view
        LoadingCache loadingCache = asyncLoadingCache.synchronous();
        Assert.assertEquals(loadingCache.get(key1), asyncLoadingCache.get(key1).get());
        Assert.assertEquals(loadingCache.get(key2), asyncLoadingCache.get(key2).get());

        String key3 = "key3";
        Assert.assertEquals(asyncLoadingCache.get(key3).get(), key3 + key3);
        Assert.assertEquals(loadingCache.asMap().get(key3), key3 + key3);

        String key4 = "key4";
        Assert.assertEquals(loadingCache.get(key4), key4 + key4);
        Assert.assertEquals(asyncLoadingCache.asMap().get(key4).get(), key4 + key4);
    }
}
