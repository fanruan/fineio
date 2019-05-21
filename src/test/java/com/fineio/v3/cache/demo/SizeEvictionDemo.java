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
public class SizeEvictionDemo {

    @Test
    public void testSizeEviction() throws InterruptedException {
        LoadingCache<Integer, Integer> cache1 = Caffeine.newBuilder()
                .maximumSize(5)
                .build(key -> createExpensiveGraph(key));
        Assert.assertEquals((int)cache1.get(10),100);
        Assert.assertEquals((int)cache1.get(20),200);
        Assert.assertEquals((int)cache1.get(30),300);
        Assert.assertEquals((int)cache1.get(40),400);
        Assert.assertEquals((int)cache1.get(50),500);
        Assert.assertEquals((int)cache1.get(40),400);
        Assert.assertEquals((int)cache1.get(50),500);
        Assert.assertEquals((int)cache1.get(5),50);
        Assert.assertEquals((int)cache1.get(4),40);
        Assert.assertEquals((int)cache1.get(3),30);

        LoadingCache<Integer, Integer> cache2 = Caffeine.newBuilder()
                .maximumWeight(1000)
                .weigher((Integer key, Integer value) -> {
                    return value;
                })
                .build(key -> createExpensiveGraph(key));
        Thread.sleep(1000L);

        Assert.assertEquals(cache1.asMap().size(),5);
        Assert.assertTrue(!cache1.asMap().containsKey(10));
        Assert.assertTrue(!cache1.asMap().containsKey(20));
        Assert.assertTrue(!cache1.asMap().containsKey(30));

        Assert.assertTrue(cache1.asMap().containsKey(3));
        Assert.assertTrue(cache1.asMap().containsKey(4));
        Assert.assertTrue(cache1.asMap().containsKey(5));
        Assert.assertTrue(cache1.asMap().containsKey(40));
        Assert.assertTrue(cache1.asMap().containsKey(50));
    }

    protected Integer createExpensiveGraph(Integer k) {
        return k * 10;
    }
}
