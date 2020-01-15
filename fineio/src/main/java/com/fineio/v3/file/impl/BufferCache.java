package com.fineio.v3.file.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.thread.FineIOExecutors;
import com.fineio.v3.FineIoProperty;
import com.fineio.v3.buffer.BufferAcquireFailedException;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.memory.MemoryManager;
import com.fr.third.guava.cache.Cache;
import com.fr.third.guava.cache.CacheBuilder;
import com.fr.third.guava.cache.RemovalListener;
import com.fr.third.guava.cache.RemovalNotification;
import com.fr.third.guava.cache.Weigher;

import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2019/5/21
 *
 * @author Lucifer
 * @description
 */
public class BufferCache {

    private static final BufferCache INSTANCE = new BufferCache();
    private Cache<FileBlock, DirectBuffer> cache;
    private ScheduledExecutorService scheduledExecutorService;

    public void start() {
        scheduledExecutorService = FineIOExecutors.newSingleThreadScheduledExecutor(BufferCacheRefresher.class);
        initCache();
        initRefresher();
    }

    public void stop() {
        scheduledExecutorService.shutdownNow();
    }

    private void initRefresher() {
        scheduledExecutorService.scheduleWithFixedDelay(new BufferCacheRefresher(),
                TimeUnit.MINUTES.toSeconds(10), 10, TimeUnit.SECONDS);
    }

    private void initCache() {
        final long halfReadMem = MemoryManager.INSTANCE.getReadMemoryLimit() / 2;
        // 50% or 1G
        long maximumWeight = Math.min(halfReadMem, FineIoProperty.CACHE_MEM_LIMIT.getValue());
        cache = CacheBuilder.newBuilder()
                // 读内存上限的一半
                .maximumWeight(maximumWeight)
                .weigher(new Weigher<FileBlock, DirectBuffer>() {
                    @Override
                    public int weigh(FileBlock key, DirectBuffer value) {
                        return value.getCapInBytes();
                    }
                })
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .recordStats()
                .concurrencyLevel(2)
                .removalListener(new RemovalListener<FileBlock, DirectBuffer>() {
                    @Override
                    public void onRemoval(RemovalNotification<FileBlock, DirectBuffer> removalNotification) {
                        removalNotification.getValue().close();
                        FineIOLoggers.getLogger().debug(MessageFormat.format("fineio cache removed {0}, cause {1}",
                                removalNotification.getKey(), removalNotification.getCause()));
                    }
                })
                .build();

        FineIOLoggers.getLogger().info(String.format("fineio buffer cache maximumWeight %d", maximumWeight));
    }

    public static BufferCache get() {
        return INSTANCE;
    }

    public DirectBuffer get(FileBlock key, Callable<? extends DirectBuffer> loadFunction) {
        try {
            return cache.get(key, loadFunction);
        } catch (Exception e) {
            throw new BufferAcquireFailedException(key);
        }
    }

    public void put(FileBlock key, DirectBuffer value) {
        cache.put(key, value);
    }

    public void invalidate(Object key) {
        cache.invalidate(key);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

    private class BufferCacheRefresher implements Runnable {
        @Override
        public void run() {
            try {
                /*
                  guava惰性释放，不用就不释放，即使过期、超重
                  这里刷掉过期的，超重的
                 */
                FineIOLoggers.getLogger().info(String.format("fineio read mem %d, fineio write mem %d",
                        MemoryManager.INSTANCE.getReadMemory(), MemoryManager.INSTANCE.getWriteMemory()));

                cache.cleanUp();
            } catch (Throwable e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
    }
}