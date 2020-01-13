package com.fineio.test.file.writer;

import com.fineio.io.base.Job;
import com.fineio.io.base.JobAssist;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.writer.JobContainer;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by daniel on 2017/2/23.
 */
public class JobContainerTest extends TestCase {

    public void testContainer() throws Exception {
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);
        JobContainer container = new JobContainer();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        control.replay();
        String uri = "";
        assertTrue(container.put(new JobAssist(connector, constructor.newInstance(uri, "1"), new Job() {
            @Override
            public void doJob() {

            }
        })));

        assertFalse(container.put(new JobAssist(connector, constructor.newInstance(uri, "1"), new Job() {
            @Override
            public void doJob() {

            }
        })));
        container.get();
        assertTrue(container.isEmpty());
        assertTrue(container.put(new JobAssist(connector, constructor.newInstance(uri, "1"), new Job() {
            @Override
            public void doJob() {

            }
        })));
        assertTrue(container.put(new JobAssist(connector, constructor.newInstance(uri, "2"), new Job() {
            @Override
            public void doJob() {

            }
        })));
        assertTrue(container.put(new JobAssist(null, constructor.newInstance(uri, "1"), new Job() {
            @Override
            public void doJob() {

            }
        })));
        container.get();
        assertTrue(container.put(new JobAssist(connector, constructor.newInstance(uri, "1"), new Job() {
            @Override
            public void doJob() {

            }
        })));
        container.get();
        assertTrue(container.put(new JobAssist(connector, constructor.newInstance(uri, "2"), new Job() {
            @Override
            public void doJob() {

            }
        })));
        assertFalse(container.isEmpty());
        container.get();
        assertFalse(container.isEmpty());
        container.get();
        assertFalse(container.isEmpty());
        container.get();
        assertTrue(container.isEmpty());
    }

    public void testWait() throws Exception {

        final Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);
        final JobContainer container = new JobContainer();
        IMocksControl control = EasyMock.createControl();
        final Connector connector = control.createMock(Connector.class);
        control.replay();
        final URI uri = new URI("");
        JobAssist jobAssist = new JobAssist(connector, constructor.newInstance(uri.getPath(), "1"), new Job() {
            @Override
            public void doJob() {

            }
        });
        final JobAssist jobAssist3 = new JobAssist(connector, constructor.newInstance(uri.getPath(), "1"), new Job() {
            @Override
            public void doJob() {

            }
        });
        assertTrue(container.put(jobAssist));
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                atomicInteger.addAndGet(1);
                container.waitJob(jobAssist3);
                atomicInteger.addAndGet(1);
            }
        }).start();
        Thread.sleep(200);
        assertEquals(atomicInteger.get(), 1);
        synchronized (jobAssist3) {
            jobAssist3.notifyAll();
        }
        assertEquals(atomicInteger.get(), 1);
        Thread.sleep(200);
        assertEquals(atomicInteger.get(), 1);
        synchronized (jobAssist) {
            jobAssist.notifyAll();
        }
        Thread.sleep(200);
        assertEquals(atomicInteger.get(), 2);

        final JobAssist jobAssist2 = new JobAssist(connector, constructor.newInstance(uri.getPath(), "2"), new Job() {
            @Override
            public void doJob() {

            }
        });
        new Thread() {
            public void run() {
                atomicInteger.addAndGet(1);
                container.waitJob(jobAssist2);
                atomicInteger.addAndGet(1);
            }
        }.start();
        Thread.sleep(200);
        assertEquals(atomicInteger.get(), 3);
        synchronized (jobAssist2) {
            jobAssist2.notifyAll();
        }
        Thread.sleep(200);
        assertEquals(atomicInteger.get(), 4);

    }


    public void testMultiThread() throws Exception {
        final Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);
        final JobContainer container = new JobContainer();
        IMocksControl control = EasyMock.createControl();
        final Connector connector = control.createMock(Connector.class);
        control.replay();
        final URI uri = new URI("");
        final int len = 100;
        final boolean[] sign = new boolean[len];
        Arrays.fill(sign, false);
        Thread[] th = new Thread[len];
        for (int i = 0; i < len; i++) {
            if (i % 2 == 0) {
                th[i] = new Thread() {
                    public void run() {
                        for (int q = 0; q < len; q++) {
                            try {
                                final int k = q;
                                container.put(new JobAssist(connector, constructor.newInstance(uri.getPath(), String.valueOf(q)), new Job() {
                                    @Override
                                    public void doJob() {
                                        sign[k] = true;
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
            } else {
                th[i] = new Thread() {
                    public void run() {
                        JobAssist jobAssist = container.get();
                        if (jobAssist != null) {
                            jobAssist.doJob();
                        }
                    }
                };
            }
        }
        for (int i = 0; i < len; i++) {
            th[i].start();
        }
        for (int i = 0; i < len; i++) {
            th[i].join();
        }
        JobAssist jobAssist = null;
        while ((jobAssist = container.get()) != null) {
            jobAssist.doJob();
        }
        for (int i = 0; i < len; i++) {
            assertTrue(sign[i]);
        }
    }

}
