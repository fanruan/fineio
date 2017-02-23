package com.fineio.test.file.writer;

import com.fineio.file.FileBlock;
import com.fineio.file.writer.Job;
import com.fineio.file.writer.JobAssist;
import com.fineio.file.writer.JobContainer;
import com.fineio.file.writer.SyncManager;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by daniel on 2017/2/23.
 */
public class SyncManagerTest extends TestCase {

    volatile boolean end = false;

    public void  testMultiThread() throws Exception {
        final Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        final JobContainer container = new JobContainer();
        IMocksControl control = EasyMock.createControl();
        final URI uri = new URI("");
        final Connector connector = control.createMock(Connector.class);
        control.replay();
        final int len = 1000;
        Thread[] threads = new Thread[len];
        final AtomicInteger fff = new AtomicInteger();
        for(int i = 0; i < len; i++) {
            threads[i] = new Thread(){

                public void run() {

                    for(int i = 0; i < len; i++) {
                        final  int k = i;
                        try {
                            SyncManager.getInstance().triggerWork(new JobAssist(connector, constructor.newInstance(uri, String.valueOf(i)), new Job() {
                                @Override
                                public void doJob() {
                                    fff.addAndGet(1);
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                    }
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
        for(int i = 0; i < len; i++) {
            threads[i].start();
        }
        Field field = SyncManager.class.getDeclaredField("working_jobs");
        field.setAccessible(true);
        final AtomicInteger a = (AtomicInteger) field.get(SyncManager.getInstance());
        Field mapField = SyncManager.class.getDeclaredField("map");
        mapField.setAccessible(true);
        final JobContainer jm = (JobContainer) mapField.get(SyncManager.getInstance());
        Thread watch = new Thread() {
            public void run() {
                while(!end || !jm.isEmpty()) {
                    assertTrue(a.intValue() < Runtime.getRuntime().availableProcessors() + 1);
                }
            }
        };
        watch.start();
        for(int i = 0; i < len; i++) {
            threads[i].join();
        }
        end = true;
        watch.join();
        Field executorField = SyncManager.class.getDeclaredField("executor");
        executorField.setAccessible(true);
        ExecutorService es = (ExecutorService) executorField.get(SyncManager.getInstance());
        es.shutdown();
        es.awaitTermination(1, TimeUnit.DAYS);
        assertTrue(jm.isEmpty());
        assertEquals(a.intValue(), 0);
        System.out.println(fff);
    }

}
