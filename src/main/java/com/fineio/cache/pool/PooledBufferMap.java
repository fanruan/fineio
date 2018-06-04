package com.fineio.cache.pool;

import com.fineio.io.Buffer;
import com.fineio.v1.cache.CacheObject;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/5/31
 */
public class PooledBufferMap<B extends Buffer> {
    private Map<B, CacheObject<B>> activeMap =
            new ConcurrentHashMap<B, CacheObject<B>>();
    private Map<URI, B> keyMap = new ConcurrentHashMap<URI, B>();

    public boolean updateBuffer(B buffer) {
        synchronized (this) {
            CacheObject<B> co = activeMap.get(buffer);
            if (co != null) {
                co.updateTime();
                return true;
            }
        }
        return false;
    }

    public long getIdle(B buffer) {
        CacheObject<B> co = activeMap.get(buffer);
        if (co != null) {
            return co.getIdle();
        }
        return 0;
    }

    /**
     * put在注册的时候使用并不会将对象放到已经申请内存的队列
     *
     * @param t
     */
    public void put(B t) {
        synchronized (this) {
            CacheObject<B> co = activeMap.get(t);
            if (co == null) {
                co = new CacheObject<B>(t);
                activeMap.put(t, co);
                keyMap.put(t.getUri(), t);
            } else {
                co.updateTime();
            }
        }
    }

    public B get(URI uri) {
        if (!keyMap.containsKey(uri)) {
            return null;
        }
        CacheObject<B> co = activeMap.get(keyMap.get(uri));
        if (null != co) {
            co.updateTime();
            return co.get();
        }
        return null;
    }

    public Iterator<B> iterator() {
        return activeMap.keySet().iterator();
    }

    public void remove(B buffer) {
        synchronized (this) {
            CacheObject<B> co = activeMap.get(buffer);
            if (co == null) {
                return;
            }
            activeMap.remove(buffer);
            keyMap.remove(buffer.getUri());
        }
    }
}
