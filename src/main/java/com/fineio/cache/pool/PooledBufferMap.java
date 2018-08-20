package com.fineio.cache.pool;

import com.fineio.cache.SyncStatus;
import com.fineio.io.AbstractBuffer;
import com.fineio.io.Buffer;
import com.fineio.v1.cache.CacheLinkedMap;

import java.lang.ref.ReferenceQueue;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/5/31
 */
public class PooledBufferMap<B extends Buffer> {
    private CacheLinkedMap<B> activeMap;
    private Map<URI, B> keyMap = new ConcurrentHashMap<URI, B>();


    public PooledBufferMap(ReferenceQueue<B> referenceQueue) {
        activeMap = new CacheLinkedMap<B>(referenceQueue);
    }

    public boolean updateBuffer(B buffer) {
        return activeMap.update(buffer);
    }

    public long getIdle(B buffer) {
        return activeMap.getIdle(buffer);
    }

    /**
     * put在注册的时候使用并不会将对象放到已经申请内存的队列
     *
     * @param t
     */
    public void put(B t) {
        synchronized (this) {
            B buffer = keyMap.get(t.getUri());
            if (null == buffer) {
                keyMap.put(t.getUri(), t);
                activeMap.put(t);
            } else {
                activeMap.update(t);
            }
        }
    }

    public B get(URI uri) {
        synchronized (this) {
            B buffer = keyMap.get(uri);
            if (null != buffer) {
                activeMap.update(buffer);
                return buffer;
            }
            return null;
        }
    }

    public Iterator<B> iterator() {
        return activeMap.iterator();
    }

    public void remove(B buffer) {
        synchronized (this) {
            activeMap.remove(buffer, true);
            keyMap.remove(buffer.getUri());
        }
    }

    synchronized
    public B poll() {
        B buffer = activeMap.poll();
        if (null == buffer) {
            return null;
        }
        switch (buffer.getBufferPrivilege()) {
            case CLEANABLE:
                keyMap.remove(buffer.getUri());
                return buffer;
            case READABLE:
                if (((AbstractBuffer) buffer).getSyncStatus() != SyncStatus.SYNC) {
                    keyMap.remove(buffer.getUri());
                    return buffer;
                }
                activeMap.update(buffer);
                return null;
            default:
                activeMap.update(buffer);
                return null;
        }
    }

}
