package com.fineio.io.pool;

import com.fineio.io.file.ReadIOFile;
import com.fineio.io.read.ReadBuffer;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/4/16
 */
public class BufferPool {
    private static ConcurrentHashMap<PoolMode, BufferPool> poolMap = new ConcurrentHashMap<PoolMode, BufferPool>();
    private BufferObservablePool[] observableMaps;
    private static ScheduledExecutorService cleanOneThread;
    private static final int DEFAULT_MAP_COUNT = 10;
    private int size;
    private static final long CLEAN_ONE_TIMEOUT = 60000L;

    private BufferPool(int size) {
        this.size = size;
        observableMaps = new BufferObservablePool[size];
        for (int i = 0; i < size; i++) {
            observableMaps[i] = new BufferObservablePool();
        }
    }

    public static final BufferPool getInstance(PoolMode model) {
        return getInstance(model, DEFAULT_MAP_COUNT);
    }

    public static final BufferPool getInstance(PoolMode model, int size) {
        BufferPool pool = poolMap.get(model);
        if (null == pool) {
            synchronized (BufferPool.class) {
                pool = poolMap.get(model);
                if (null == pool) {
                    pool = new BufferPool(size);
                    poolMap.put(model, pool);
                }
            }
        }
        if (cleanOneThread == null) {
            cleanOneThread = Executors.newSingleThreadScheduledExecutor();
            cleanOneThread.scheduleAtFixedRate(new ScheduledCleanOneTask(), CLEAN_ONE_TIMEOUT, CLEAN_ONE_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        return pool;
    }

    public void registerFromBuffer(ReadBuffer buffer, boolean force) {
        URI uri = buffer.getUri();
        observableMaps[getIndex(uri)].registerFromBuffer(buffer, force);
    }

    public void registerFromIOFile(URI uri, ReadIOFile ioFile) {
        observableMaps[getIndex(uri)].registerFromIOFile(uri, ioFile);
    }

    public boolean cleanOne() {
        boolean clean = false;
        for (int i = 0; i < size; i++) {
            if (observableMaps[i].cleanOne()) {
                clean = true;
            }
        }
        return clean;
    }

    public void cleanAll() {
        for (int i = 0; i < size; i++) {
            observableMaps[i].cleanAll();
        }
    }

    public ReadBuffer getBuffer(URI uri) {
        return observableMaps[getIndex(uri)].getBuffer(uri);
    }

    public void cleanByKey(URI uri) {
        observableMaps[getIndex(uri)].clean(uri);
    }

    private int getIndex(URI uri) {
        int hash = Math.abs(uri.hashCode());
        return hash % size;
    }

    public static boolean cleanOneEachMode() {
        Iterator<Map.Entry<PoolMode, BufferPool>> iterator = poolMap.entrySet().iterator();
        boolean clean = false;
        while (iterator.hasNext()) {
            if (iterator.next().getValue().cleanOne()) {
                clean = true;
            }
        }
        return clean;
    }

    public static void cleanAllEachMode() {
        Iterator<Map.Entry<PoolMode, BufferPool>> iterator = poolMap.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().cleanAll();
        }
    }

    private static class ScheduledCleanOneTask implements Runnable {

        @Override
        public void run() {
            cleanOneEachMode();
        }
    }

}
