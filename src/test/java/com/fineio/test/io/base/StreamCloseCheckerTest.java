package com.fineio.test.io.base;

import com.fineio.v2.io.base.StreamCloseChecker;
import junit.framework.TestCase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by daniel on 2017/2/24.
 */
public class StreamCloseCheckerTest extends TestCase{

    public void testCheck() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);

        StreamCloseChecker closeChecker = new StreamCloseChecker(atomicInteger.get()) {
            @Override
            public boolean check() {
                return atomicInteger.get() == getStatus();
            }
        };
        assertTrue(closeChecker.check());
        atomicInteger.set(1);
        assertFalse(closeChecker.check());
        atomicInteger.set(1);
        assertFalse(closeChecker.check());
        atomicInteger.set(0);
        assertTrue(closeChecker.check());

    }
}
