package com.fineio.test.base;

import com.fineio.base.SingleWaitThread;
import com.fineio.base.Worker;
import com.fineio.test.io.MemoryLeakTest;
import junit.framework.TestCase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by daniel on 2017/3/6.
 */
public class SingleWaitThreadTest  extends TestCase {

    public void testSingleWaitThread() {
        final AtomicInteger running = new AtomicInteger(0);
        final AtomicInteger trigger = new AtomicInteger(0);
        final SingleWaitThread thread = new SingleWaitThread(new Worker() {
            public void work() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                running.addAndGet(1);
            }
        });
        final int threadLen = 100;
        Thread[] t = new Thread[threadLen];
        for(int i = 0; i < threadLen; i++) {
            t[i] = new Thread(){
                public void run(){
                    for(int k = 0; k < threadLen; k++) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
                        thread.triggerWork();
                        trigger.addAndGet(1);
                    }
                }
            };
        }
        for(int i = 0; i < threadLen; i++) {
            t[i].start();
        }
        for(int i = 0; i < threadLen; i++) {
            try {
                t[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Thread ttt = new Thread() {
            public void run(){
                try {
                    Thread.sleep(2010);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                thread.clear();
            }
        };
        try {
            ttt.start();
            ttt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(trigger.get(), threadLen * threadLen);
        assertTrue(trigger.get() > running.get());
        assertTrue(running.get() > 1);
        MemoryLeakTest.assertZeroMemory();
    }

}
