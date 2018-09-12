package com.fineio.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author yee
 * @date 2018/9/12
 */
public class FineIOExecutors {
    public static ScheduledExecutorService newScheduledExecutorService(int threadCount, Class clazz) {
        return Executors.newScheduledThreadPool(threadCount, new FineIOThreadFactory(clazz));
    }

    public static ScheduledExecutorService newScheduledExecutorService(int threadCount, String prefix) {
        return Executors.newScheduledThreadPool(threadCount, new FineIOThreadFactory(prefix));
    }

    public static ExecutorService newCachedExecutorService(String prefix) {
        return Executors.newCachedThreadPool(new FineIOThreadFactory(prefix));
    }

    public static ExecutorService newCachedExecutorService(Class clazz) {
        return Executors.newCachedThreadPool(new FineIOThreadFactory(clazz));
    }

    public static ExecutorService newSingleThreadExecutor(Class clazz) {
        return Executors.newSingleThreadExecutor(new FineIOThreadFactory(clazz));
    }

    public static ExecutorService newFixedThreadPool(int threadCount, Class clazz) {
        return Executors.newFixedThreadPool(threadCount, new FineIOThreadFactory(clazz));
    }

    public static ExecutorService newFixedThreadPool(int threadCount, String prefix) {
        return Executors.newFixedThreadPool(threadCount, new FineIOThreadFactory(prefix));
    }
}
