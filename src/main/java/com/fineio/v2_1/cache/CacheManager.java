package com.fineio.v2_1.cache;

import com.fineio.v2_1.unsafe.UnsafeBuf;

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
            final URI uri = buf.getBufferKey().getBlock().getBlockURI();
            synchronized (getBufferLock(uri)) {
                buffers.put(uri, buf);
            }
        }
    }

    public UnsafeBuf get(URI bufferKey, BufferCreator creator) {
        synchronized (getBufferLock(bufferKey)) {
            UnsafeBuf buf = buffers.get(bufferKey);
            if (null == buf) {
                buf = creator.createBuffer();
                buf.loadContent();
                buffers.put(bufferKey, buf);
            }
            return buf;
        }
    }

    private Object getBufferLock(URI className) {
        Object lock = this;
        if (lockMap != null) {
            Object newLock = new Object();
            lock = lockMap.putIfAbsent(className, newLock);
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
