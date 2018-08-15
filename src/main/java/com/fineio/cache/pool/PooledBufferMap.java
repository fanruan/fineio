package com.fineio.cache.pool;

import com.fineio.cache.BufferPrivilege;
import com.fineio.io.Buffer;
import com.fineio.v1.cache.CacheLinkedMap;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/5/31
 */
public class PooledBufferMap<B extends Buffer> {
    private CacheLinkedMap<B> activeMap = new CacheLinkedMap<B>();
    private Map<URI, B> keyMap = new ConcurrentHashMap<URI, B>();


    public PooledBufferMap() {
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
        if (!keyMap.containsKey(uri)) {
            return null;
        }
        B buffer = keyMap.get(uri);
        activeMap.update(buffer);
        return buffer;
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
        if (buffer.getBufferPrivilege().compareTo(BufferPrivilege.EDITABLE) < 0) {
            keyMap.remove(buffer.getUri());
            return buffer;
        } else {
            activeMap.update(buffer);
            return null;
        }
    }
}
