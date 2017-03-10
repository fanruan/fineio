package com.fineio.test.memory;

import com.fineio.memory.MemoryHelper;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * Created by daniel on 2017/2/13.
 */
public class MemoryHelperTest extends TestCase {

    public void testMemoryGet(){
        try {
            Method method = MemoryHelper.class.getDeclaredMethod("getMaxMemory");
            method.setAccessible(true);
            long res = (Long)method.invoke(null);
            assertTrue(res > 0);
            System.out.println((res >> 20) + "MB max");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Method method = MemoryHelper.class.getDeclaredMethod("getFreeMemory");
            method.setAccessible(true);
            long res = (Long)method.invoke(null);
            assertTrue(res > 0);
            System.out.println((res >> 20) + "MB free");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
