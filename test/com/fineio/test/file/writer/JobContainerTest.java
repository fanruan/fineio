package com.fineio.test.file.writer;

import com.fineio.file.FileBlock;
import com.fineio.file.writer.Job;
import com.fineio.file.writer.JobAssist;
import com.fineio.file.writer.JobContainer;
import com.fineio.file.writer.SyncKey;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;

/**
 * Created by daniel on 2017/2/23.
 */
public class JobContainerTest extends TestCase {

    public void testContainer() throws Exception {
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        JobContainer container = new JobContainer();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        control.replay();
        URI uri = new URI("");
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



    public void testMultiThread() throws Exception {
        final Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        final JobContainer container = new JobContainer();
        IMocksControl control = EasyMock.createControl();
        final Connector connector = control.createMock(Connector.class);
        control.replay();
        final URI uri = new URI("");
        final int len = 100;
        final boolean[] sign = new  boolean[len];
        Arrays.fill(sign, false);
        Thread[] th = new Thread[len];
        for(int i = 0; i < len; i ++) {
            if(i %2 == 0) {
                th[i] = new Thread() {
                    public void run() {
                        for (int q = 0; q < len; q++) {
                            try {
                                final int k = q;
                                container.put(new JobAssist(connector, constructor.newInstance(uri, String.valueOf(q)), new Job() {
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
                        if(jobAssist != null){
                            jobAssist.doJob();
                        }
                    }
                };
            }
        }
        for(int i = 0; i < len; i ++) {
            th[i].start();
        }
        for(int i = 0; i < len; i ++) {
            th[i].join();
        }
        JobAssist jobAssist = null;
        while ((jobAssist = container.get()) != null) {
            jobAssist.doJob();
        }
        for(int i = 0; i < len; i ++) {
            assertEquals(sign[i], true);
        }
    }

}
