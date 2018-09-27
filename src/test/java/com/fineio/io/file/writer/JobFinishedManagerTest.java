package com.fineio.io.file.writer;

import com.fineio.io.file.writer.task.Pair;
import com.fineio.logger.FineIOLoggers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018/7/13
 */
public class JobFinishedManagerTest {

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static List<URI> uris = new ArrayList<URI>();
    private static Future<Pair<URI, Boolean>> futures[];

    @BeforeClass
    public static void before() {
        int total = (int) (1 + Math.random() * 100);
        FineIOLoggers.getLogger().info("total " + total);
        futures = new Future[total];
        for (int i = 0; i < total; i++) {
            uris.add(URI.create("uri_" + i));
            final int finalI = i;
            futures[i] = new Future<Pair<URI, Boolean>>() {
                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }

                @Override
                public boolean isDone() {
                    return false;
                }

                @Override
                public Pair<URI, Boolean> get() throws InterruptedException, ExecutionException {
                    // 模拟获取延时
                    Thread.sleep(100);
                    FineIOLoggers.getLogger().info("consume uri_" + finalI);
                    return new Pair<URI, Boolean>(URI.create("uri_" + finalI), true);
                }

                @Override
                public Pair<URI, Boolean> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return null;
                }
            };
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
                            Field field = JobFinishedManager.class.getDeclaredField("map");
                            field.setAccessible(true);
                            JobFinishedManager.TaskMap map = (JobFinishedManager.TaskMap) field.get(JobFinishedManager.getInstance());
                            System.out.println(map.isEmpty());
                            assertTrue(map.isEmpty());
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
                        for (Future<Pair<URI, Boolean>> future : futures) {
                            try {
                                JobFinishedManager.getInstance().submit(future);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                latch.countDown();
            }
        }).start();
        latch.await();
    }

}