package com.fineio.test.file;

import com.fineio.io.file.FileBlock;
import com.fineio.test.io.MemoryLeakTest;
import junit.framework.TestCase;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public class FileBlockTest extends TestCase {

    public void testEquals() throws  Exception {
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        URI a = new URI("a");
        URI b = new URI("a");
        URI c = new URI("c");
        assertEquals(constructor.newInstance(a, "test"), constructor.newInstance(b, "test"));
        assertNotSame(constructor.newInstance(a, "test"), constructor.newInstance(b, "test1"));
        assertNotSame(constructor.newInstance(a, "test"), constructor.newInstance(c, "test"));
        assertEquals(constructor.newInstance(a, "test").getParentUri(), a);
        assertEquals(constructor.newInstance(b, "test").getParentUri(), b);
        assertEquals(constructor.newInstance(c, "test").getFileName(), "test");
        MemoryLeakTest.assertZeroMemory();
    }

    public void testToString() throws  Exception {
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        URI a = new URI("a");
        assertEquals(constructor.newInstance(a, "test").toString(), "a" + "test");
        MemoryLeakTest.assertZeroMemory();
    }
}
