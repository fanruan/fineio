package com.fineio.v3.cache.demo;

import com.fineio.v3.cache.core.Caffeine;
import com.fineio.v3.cache.core.LoadingCache;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class created on 2019/5/21
 *
 * @author Lucifer
 * @description
 */
public class ReferenceEvictionDemo extends BaseDemo {

    @Test
    public void testReferenceEviction() {
        LoadingCache<String, String> cache1 = Caffeine.newBuilder()
                .weakKeys()
                .weakValues()
                .build(key -> createExpensiveGraph(key));
        String key1 = "key1";
        String key2 = "key2";
        Assert.assertEquals(cache1.get(key1), key1 + key1);
        Assert.assertEquals(cache1.get(key2), key2 + key2);

        Assert.assertTrue(cache1.asMap().containsKey(key1));
        Assert.assertTrue(cache1.asMap().containsKey(key2));
        System.gc();
        Assert.assertTrue(!cache1.asMap().containsKey("key1"));
        Assert.assertTrue(!cache1.asMap().containsKey(key2));


        LoadingCache<String, String> cache2 = Caffeine.newBuilder()
                .softValues()
                .build(key -> createExpensiveGraph(key));

    }
}
