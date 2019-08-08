package com.fineio.test.file;

import com.fineio.io.file.FileBlock;
import junit.framework.TestCase;

import java.lang.reflect.Constructor;

/**
 * Created by daniel on 2017/2/10.
 */
public class FileBlockTest extends TestCase {

    public void testEquals() throws Exception {
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);
        assertEquals(constructor.newInstance("a", "test"), constructor.newInstance("a", "test"));
        assertNotSame(constructor.newInstance("a", "test"), constructor.newInstance("a", "test1"));
        assertNotSame(constructor.newInstance("a", "test"), constructor.newInstance("c", "test"));
        assertEquals(constructor.newInstance("a", "test").getDir(), "a");
        assertEquals(constructor.newInstance("b", "test").getDir(), "b");
        assertEquals(constructor.newInstance("c", "test").getName(), "test");
    }

    public void testToString() throws Exception {
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);
        assertEquals(constructor.newInstance("a", "test").toString(), "a/test");
    }
}
