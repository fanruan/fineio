package com.fineio.test.cache;

import com.fineio.cache.AtomicWatchLong;
import com.fineio.cache.Watcher;
import junit.framework.TestCase;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by daniel on 2017/3/3.
 */
public class AtomicWatchLongTest extends TestCase {


    public void testWatch() {

        AtomicWatchLong v = new AtomicWatchLong();
        final AtomicBoolean watched = new AtomicBoolean(false);
        v.addListener(new Watcher() {
            public void watch(long change) {
                watched.set(true);
            }
        });
        v.addListener(new Watcher() {
            public void watch(long change) {
                assertEquals(change, 10);
            }
        });
        assertEquals(10, v.add(10));
        assertTrue(watched.get());
        watched.set(false);
        assertEquals(10, v.add(0));
        assertFalse(watched.get());
        assertEquals(v.get(), 10);
    }
}
