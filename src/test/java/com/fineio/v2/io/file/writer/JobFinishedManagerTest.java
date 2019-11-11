package com.fineio.v2.io.file.writer;

import com.fineio.io.base.BufferKey;
import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.v2.io.base.Job;
import com.fineio.v2.io.base.JobAssist;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018/7/13
 */
@Ignore
public class JobFinishedManagerTest {

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static List<URI> uris = new ArrayList<URI>();
    private static List<JobAssist> jobAssists = new ArrayList<JobAssist>();

    @BeforeClass
    public static void before() {
        int total = (int) (1 + Math.random() * 100);
        FineIOLoggers.getLogger().info("total " + total);
        for (int i = 0; i < total; i++) {
            URI uri = URI.create("uri_" + i);
            uris.add(uri);
            jobAssists.add(new JobAssist(new BufferKey(null, new FileBlock(uri)), new Job() {
                @Override
                public void doJob() {
//                    try {
//                        TimeUnit.MILLISECONDS.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }));
        }

    }

    @Test
    public void testManager() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (URI uri : uris) {
                    JobFinishedManager.getInstance().addTask(uri);
                    FineIOLoggers.getLogger().info("addTask " + uri);
                }
                JobFinishedManager.getInstance().finish(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Field field = JobFinishedManager.class.getDeclaredField("queue");
                            field.setAccessible(true);
                            Queue queue = (Queue) field.get(JobFinishedManager.getInstance());
                            System.out.println(queue.isEmpty());
                            assertTrue(queue.isEmpty());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        latch.countDown();
                    }
                });
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (JobAssist jobAssist : jobAssists) {
                            SyncManager.getInstance().force(jobAssist);
                        }
                    }
                });
                latch.countDown();
            }
        }).start();
        latch.await();
    }

}