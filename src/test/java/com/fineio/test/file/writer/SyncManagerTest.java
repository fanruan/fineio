package com.fineio.test.file.writer;

import com.fineio.io.base.Job;
import com.fineio.io.base.JobAssist;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.writer.JobContainer;
import com.fineio.io.file.writer.SyncManager;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by daniel on 2017/2/23.
 */
public class SyncManagerTest extends TestCase {

    volatile boolean end = false;

    public void  testMultiThread() throws Exception {
        final Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);
        IMocksControl control = EasyMock.createControl();
        final String uri = "";
        final Connector connector = control.createMock(Connector.class);
        control.replay();
        final int len = 1000;
        Thread[] threads = new Thread[len];
        final AtomicInteger fff = new AtomicInteger();
        final Map<Integer, Object> runningCheck = new ConcurrentHashMap<Integer, Object>();

        for(int i = 0; i < len; i++) {
            threads[i] = new Thread(){

                public void run() {

                    for(int i = 0; i < len; i++) {
                        final  int k = i;
                        try {
                            SyncManager.getInstance().triggerWork(new JobAssist(connector, constructor.newInstance(uri, String.valueOf(i)), new Job() {
                                public void doJob() {
                                    assertFalse(runningCheck.containsKey(k));
                                    runningCheck.put(k, this);
                                    fff.addAndGet(1);
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                    }
                                    runningCheck.remove(k);
                                }
                            }));
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }

                }
            };
        }


        Thread[] awaitThreads = new Thread[len];
        for(int i = 0; i < len; i++) {
            final  int k = i;
            awaitThreads[i] =  new Thread(){
                public void run() {
                    try {
                        SyncManager.getInstance().force(new JobAssist(connector, constructor.newInstance(uri, String.valueOf(k)), new Job() {
                            public void doJob() {
                                assertFalse(runningCheck.containsKey(k));
                                runningCheck.put(k, this);
                                fff.addAndGet(1);
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                }
                                runningCheck.remove(k);
                            }
                        }));
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            };
        }

        for(int i = 0; i < len; i++) {
            awaitThreads[i].start();
            threads[i].start();
        }
        SyncManager.getInstance().setThreads(SyncManager.getInstance().getThreads()*2);
        Field field = SyncManager.class.getDeclaredField("working_jobs");
        field.setAccessible(true);
        final AtomicInteger a = (AtomicInteger) field.get(SyncManager.getInstance());
        Field mapField = SyncManager.class.getDeclaredField("map");
        mapField.setAccessible(true);
        final JobContainer jm = (JobContainer) mapField.get(SyncManager.getInstance());
        Thread watch = new Thread() {
            public void run() {
                while(!end || !jm.isEmpty()) {
                    assertTrue(a.intValue() < SyncManager.getInstance().getThreads() + 1);
                }
            }
        };
        watch.start();
        Thread.sleep(100);
        for(int i = 0; i < len; i++) {
            SyncManager.getInstance().triggerWork(new JobAssist(connector, constructor.newInstance(uri, String.valueOf(i)), new Job() {
                public void doJob() {
                    fff.addAndGet(1);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            }));
        }
        for(int i = 0; i < len; i++) {
            threads[i].join();
            awaitThreads[i].join();
        }
        end = true;
        watch.join();
        Field executorField = SyncManager.class.getDeclaredField("executor");
        executorField.setAccessible(true);
        ExecutorService es = (ExecutorService) executorField.get(SyncManager.getInstance());
        es.shutdown();
        es.awaitTermination(1, TimeUnit.DAYS);
        SyncManager.release();
        assertTrue(jm.isEmpty());
        assertEquals(a.intValue(), 0);
        assertTrue(fff.intValue() >= len);
        System.out.println(fff);
    }

}
