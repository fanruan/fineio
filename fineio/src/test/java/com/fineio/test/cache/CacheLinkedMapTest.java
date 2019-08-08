package com.fineio.test.cache;

import com.fineio.cache.CacheLinkedMap;
import junit.framework.TestCase;

import java.util.Iterator;

/**
 * Created by daniel on 2017/3/7.
 */
public class CacheLinkedMapTest extends TestCase {


    public void testMap() {
        CacheLinkedMap<String> map = new CacheLinkedMap<String>();
        map.put("a");
        map.put("b");
        map.put("c");
        map.put("d");
        map.put("e");
        map.put("f");
        map.put("g");
        assertEquals(map.peek(), "a");
        assertEquals(map.poll(), "a");
//        map.update("a");
        map.update("b");
        map.update("c");
        map.update("d");
        map.update("e");
        map.update("f");
        map.update("g");
//        assertEquals(map.peek(), "a");
//        assertEquals(map.poll(), "a");
        assertEquals(map.poll(), "b");
        assertEquals(map.poll(), "c");
        map.put("d");
        map.update("d");
        assertEquals(map.poll(), "e");
        assertEquals(map.poll(), "f");
        assertEquals(map.poll(), "g");
        assertEquals(map.poll(), "d");
        assertNull(map.poll());
        map.put("d");
        map.update("d");
        assertEquals(map.poll(), "d");
        assertNull(map.poll());
        map.put("h");
        map.update("h");
        map.put("f");
        map.update("f");
        assertEquals(map.poll(), "h");
        assertEquals(map.poll(), "f");
        assertNull(map.poll());
        assertTrue(map.update("a"));
        assertTrue(map.update("a"));
        assertEquals(map.peek(), "a");
        assertEquals(map.poll(), "a");
        assertNull(map.poll());
        assertFalse(map.update("q"));
        assertNull(map.poll());
        map.put("a");
        map.put("a");
        map.put("a");
        map.update("a");
        map.update("a");
        map.update("a");
        assertEquals(map.poll(), "a");
        assertEquals(map.poll(), null);
        map.remove("a", true);
        assertEquals(map.poll(), null);
        Iterator<String> iter = map.iterator();
        assertTrue(iter.hasNext());
        while (iter.hasNext()) {
            assertFalse("a".equals(iter.next()));
        }
    }

    public void testCycle() {
        CacheLinkedMap<String> map = new CacheLinkedMap<String>();
        map.put("a");
        map.put("b");
        map.put("c");
        map.put("a");
        map.put("b");
        map.put("c");
        assertTrue(map.contains("a"));
        assertEquals(map.poll(), "a");
        map.update("b");
        map.update("c");
        map.remove("c", false);
        assertTrue(map.update("c"));
        assertTrue(map.contains("a"));
        assertEquals(map.poll(), "b");
        assertEquals(map.poll(), "c");
        assertTrue(map.contains("c"));
        assertEquals(map.poll(), null);
        assertTrue(map.contains("b"));
    }
}
