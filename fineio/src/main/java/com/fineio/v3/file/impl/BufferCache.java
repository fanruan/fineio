package com.fineio.v3.file.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.thread.FineIOExecutors;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.cache.core.Cache;
import com.fineio.v3.cache.core.Caffeine;
import com.fineio.v3.memory.MemoryManager;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * This class created on 2019/5/21
 *
 * @author Lucifer
 * @description
 */
public class BufferCache {

    private static final BufferCache INSTANCE = new BufferCache();
    private Cache<FileBlock, DirectBuffer> cache;

    private BufferCache() {
        initCache();
        initRefresher();
    }

    private void initRefresher() {
        FineIOExecutors.newSingleThreadScheduledExecutor(BufferCacheRefresher.class)
                .scheduleWithFixedDelay(new BufferCacheRefresher(), TimeUnit.MINUTES.toSeconds(10), 10, TimeUnit.SECONDS);
    }

    private void initCache() {
        final long maximumWeight = MemoryManager.INSTANCE.getCacheMemoryLimit() / 2;
        cache = Caffeine.newBuilder()
                // 读内存上限的一半
                .maximumWeight(maximumWeight)
                .<FileBlock, DirectBuffer>weigher((key, value) -> value.getSizeInBytes())
                .expireAfterAccess(Duration.ofMinutes(10))
                .recordStats()
                .removalListener((key, value, cause) -> {
                    value.close();
                    FineIOLoggers.getLogger().debug(MessageFormat.format("removed {0}, cause {1}", key, cause));
                })
                .build();

        FineIOLoggers.getLogger().info(String.format("fineio buffer cache maximumWeight %d", maximumWeight));
    }

    public static BufferCache get() {
        return INSTANCE;
    }

    public DirectBuffer get(FileBlock key, Function<? super FileBlock, ? extends DirectBuffer> mappingFunction) {
        return cache.get(key, mappingFunction);
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
                  caffine惰性释放，不用就不释放，即使过期、超重
                  这里刷掉过期的，超重的
                 */
                cache.cleanUp();
            } catch (Throwable e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
    }
}
