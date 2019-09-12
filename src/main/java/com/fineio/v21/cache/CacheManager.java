package com.fineio.v21.cache;

import com.fineio.v21.unsafe.UnsafeBuf;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yee
 * @date 2019/9/12
 */
public class CacheManager {

    private ConcurrentHashMap<URI, UnsafeBuf> buffers = new ConcurrentHashMap<URI, UnsafeBuf>();
    private ConcurrentMap<URI, Object> lockMap = new ConcurrentHashMap<URI, Object>();

    private CacheManager() {
    }

    public static CacheManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void put(UnsafeBuf buf) {
        if (null != buf) {
            final URI uri = buf.getUri();
            synchronized (getBufferLock(uri)) {
                buffers.put(uri, buf);
            }
        }
    }

    public UnsafeBuf get(URI uri, BufferCreator creator) {
        synchronized (getBufferLock(uri)) {
            UnsafeBuf buf = buffers.get(uri);
            if (null == buf) {
                buf = creator.createBuffer();
                buffers.put(uri, buf);
            }
            return buf;
        }
    }

    private Object getBufferLock(URI uri) {
        Object lock = this;
        if (lockMap != null) {
            Object newLock = new Object();
            lock = lockMap.putIfAbsent(uri, newLock);
            if (lock == null) {
                lock = newLock;
            }
        }
        return lock;
    }

    public interface BufferCreator {
        UnsafeBuf createBuffer();
    }

    private static class SingletonHolder {
        private static CacheManager INSTANCE = new CacheManager();
    }
}
