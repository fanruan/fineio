package com.fineio.test.cache;

import com.fineio.cache.Allocator;
import com.fineio.cache.NewAllocator;
import com.fineio.cache.ReAllocator;
import junit.framework.TestCase;

/**
 * Created by daniel on 2017/3/6.
 */
public class AllocatorTest extends TestCase {

    public void testNewAllocator() {
        Allocator allocator = new NewAllocator(1000);
        assertEquals(1000, allocator.getChangeSize());
    }


    public void testReAllocator() {
        Allocator allocator = new ReAllocator(0, 400, 1000);
        assertEquals(600, allocator.getChangeSize());
    }
}
