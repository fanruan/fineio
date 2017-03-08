package com.fineio.test.cache;

import com.fineio.cache.CacheObject;
import com.fineio.cache.LEVEL;
import junit.framework.TestCase;

/**
 * Created by daniel on 2017/3/3.
 */
public class CacheObjectTest extends TestCase {

    public void testObject() {
        CacheObject<String> co = new CacheObject<String>("SSS");
        long t = System.currentTimeMillis();
        co.updateTime();
        assertEquals(co.get(), "SSS");
        assertTrue(co.getIdle() <= (System.currentTimeMillis() - t));



    }
}
