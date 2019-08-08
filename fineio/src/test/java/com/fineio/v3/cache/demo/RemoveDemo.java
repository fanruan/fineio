package com.fineio.v3.cache.demo;

import com.fineio.v3.cache.core.Cache;
import com.fineio.v3.cache.core.Caffeine;
import com.fineio.v3.cache.core.RemovalCause;
import com.fineio.v3.cache.core.RemovalListener;
import org.junit.Assert;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2019/5/21
 *
 * @author Lucifer
 * @description
 */
public class RemoveDemo {

    @Test
    public void testInvalidate() {
        Cache<String, String> manualCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();

        ConcurrentMap<String, String> asMap = manualCache.asMap();
        String key = "key";
        manualCache.put(key, key + key);
        Assert.assertEquals(manualCache.getIfPresent(key), key + key);
        Assert.assertEquals(asMap.get(key), key + key);
        manualCache.invalidate(key);
        Assert.assertFalse(asMap.containsKey(key));
    }

    @Test
    public void testRemoval() {

        RemovalListener removalListener = Mockito.mock(RemovalListener.class);
        String key = "key";
        String value = "key+key";

        Cache<String, String> manualCache = Caffeine.newBuilder()
                .removalListener(removalListener)
                .build();

        manualCache.put(key, value);
        manualCache.invalidate(key);
        Mockito.verify(removalListener).onRemoval(key, value, RemovalCause.EXPLICIT);
    }

}
