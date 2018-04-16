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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author yee
 * @date 2018/4/16
 */
public class BufferObservablePool {
    private ConcurrentHashMap<URI, BufferObservable> observableMap;

    public BufferObservablePool() {
        observableMap = new ConcurrentHashMap<URI, BufferObservable>();
    }

    public void registerFromBuffer(ReadBuffer buffer, boolean force) {
        URI uri = buffer.getUri();
        BufferObservable observable = observableMap.get(uri);
        if (null == observable) {
            observable = BufferObservable.newInstance(uri);
            observable.setBuffer(buffer, false);
        } else if (!observable.isBufferValid()) {
            observable.setBuffer(buffer, false);
        } else if (force) {
            observable.setBuffer(buffer, true);
            observable.bufferChanged();
        }
        observableMap.put(uri, observable);
    }

    public void registerFromIOFile(URI uri, ReadIOFile ioFile) {
        BufferObservable observable = observableMap.get(uri);
        if (null == observable) {
            observable = BufferObservable.newInstance(uri);
        }
        observable.registerObserver(ioFile);
        observableMap.put(uri, observable);
    }

    public boolean cleanOne() {
        boolean clean = false;
        List<BufferObservable> list = new ArrayList<BufferObservable>(observableMap.values());
        if (!list.isEmpty()) {
            Collections.sort(list);
            BufferObservable observable = list.get(0);
            observableMap.remove(observable.getUri());
            observable.bufferCleaned();
            clean = true;
        }
        return clean;
    }

    public void cleanAll() {
        ConcurrentHashMap<URI, BufferObservable> map = new ConcurrentHashMap<URI, BufferObservable>(observableMap);
        observableMap.clear();
        Iterator<Map.Entry<URI, BufferObservable>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().bufferCleaned();
        }
        map.clear();
    }

    public ReadBuffer getBuffer(URI uri) {
        BufferObservable observable = observableMap.get(uri);
        if (null == observable) {
            return null;
        }
        return observable.getBuffer();
    }
}
