package com.fineio.io.pool;

import com.fineio.io.file.ReadIOFile;
import com.fineio.io.read.ReadBuffer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
    private ConcurrentHashMap<URI, BufferObservable>[] observableMaps;
    private static ScheduledExecutorService cleanOneThread;
//    private static ScheduledExecutorService cleanAllThread;
    private static final int DEFAULT_MAP_COUNT = 10;
    private int size;
    private static final long CLEAN_ONE_TIMEOUT = 60000L;
//    private static final long CLEAN_ALL_TIMEOUT = 10 * 60000L;

    private BufferPool(int size) {
        this.size = size;
        observableMaps = new ConcurrentHashMap[size];
        for (int i = 0; i < size; i++) {
            observableMaps[i] = new ConcurrentHashMap<URI, BufferObservable>();
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

//        if (cleanAllThread == null) {
//            cleanAllThread = Executors.newSingleThreadScheduledExecutor();
//            cleanAllThread.scheduleAtFixedRate(new ScheduledCleanAllTask(), CLEAN_ALL_TIMEOUT, CLEAN_ALL_TIMEOUT, TimeUnit.MILLISECONDS);
//        }
        return pool;
    }

    public void registerFromBuffer(ReadBuffer buffer, boolean force) {
        URI uri = buffer.getUri();
        ConcurrentHashMap<URI, BufferObservable> current = observableMaps[getIndex(uri)];
        BufferObservable observable = current.get(uri);
        if (null == observable) {
            observable = BufferObservable.newInstance(uri);
            observable.setBuffer(buffer, false);
        } else if (!observable.isBufferValid()) {
            observable.setBuffer(buffer, false);
        } else if (force) {
            observable.setBuffer(buffer, true);
            observable.bufferChanged();
        }
        current.put(uri, observable);
    }

    public void registerFromIOFile(URI uri, ReadIOFile ioFile) {
        ConcurrentHashMap<URI, BufferObservable> current = observableMaps[getIndex(uri)];
        BufferObservable observable = current.get(uri);
        if (null == observable) {
            observable = BufferObservable.newInstance(uri);
        }
        observable.registerObserver(ioFile);
        current.put(uri, observable);
    }

    public boolean cleanOne() {
        boolean clean = false;
        for (int i = 0; i < size; i++) {
            List<BufferObservable> list = new ArrayList<BufferObservable>(observableMaps[i].values());
            if (!list.isEmpty()) {
                Collections.sort(list);
                BufferObservable observable = list.get(0);
                observableMaps[i].remove(observable.getUri());
                observable.bufferCleaned();
                clean = true;
            }
        }
        return clean;
    }

    public void cleanAll() {
        for (int i = 0; i < size; i++) {
            ConcurrentHashMap<URI, BufferObservable> map = new ConcurrentHashMap<URI, BufferObservable>(observableMaps[i]);
            observableMaps[i].clear();
            Iterator<Map.Entry<URI, BufferObservable>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                iterator.next().getValue().bufferCleaned();
            }
            map.clear();
        }
    }

    public ReadBuffer getBuffer(URI uri) {
        ConcurrentHashMap<URI, BufferObservable> current = observableMaps[getIndex(uri)];
        BufferObservable observable = current.get(uri);
        if (null == observable) {
            return null;
        }
        return observable.getBuffer();
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
            cleanAllEachMode();
        }
    }

//    private static class ScheduledCleanAllTask implements Runnable {
//
//        @Override
//        public void run() {
//            cleanAllEachMode();
//        }
//    }
}
