package com.fineio.v3.file.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.cache.core.Cache;
import com.fineio.v3.cache.core.Caffeine;
import com.fineio.v3.memory.MemoryManager;

import java.text.MessageFormat;
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
        cache = Caffeine.newBuilder()
                // 读内存上限-4M
                .maximumWeight(MemoryManager.INSTANCE.getCacheMemoryLimit() - (1 << 22))
                .<FileBlock, DirectBuffer>weigher((key, value) -> value.getSizeInBytes())
                .expireAfterAccess(60, TimeUnit.MINUTES)
                .recordStats()
                .removalListener((key, value, cause) -> {
                    value.close();
                    FineIOLoggers.getLogger().debug(MessageFormat.format("removed {0}, cause {1}", key, cause));
                })
                .build();
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
}