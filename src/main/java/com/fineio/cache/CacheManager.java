package com.fineio.cache;

import com.fineio.cache.pool.BufferPool;
import com.fineio.cache.pool.PoolMode;
import com.fineio.io.AbstractBuffer;
import com.fineio.io.Buffer;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yee
 * @date 2018/5/31
 */
public class CacheManager {
    private volatile static CacheManager instance;
    public static final int MAX_AUTO_FREE_COUNT = 3;

    private ConcurrentHashMap<PoolMode, BufferPool> poolMap;
    private MemoryHandler memoryHandler;
    public ReferenceQueue<? extends Buffer> referenceQueue;
    private AtomicLong maxExistsBuffer;
    private AtomicInteger limitCount = new AtomicInteger(0);
    private MemoryHandler.GcCallBack callBack;


    private CacheManager() {
        callBack = createGCCallBack();
        memoryHandler = MemoryHandler.newInstance(callBack);
        long maxSize = MemoryHandler.getMaxMemory() >> 22;
        maxExistsBuffer = new AtomicLong(maxSize);
        referenceQueue = new ReferenceQueue<Buffer>();
        poolMap = new ConcurrentHashMap<PoolMode, BufferPool>();
    }

    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }

    public static void clear() {
        synchronized (CacheManager.class) {
//            instance = null;
            CacheManager cm = null;
            if (null != instance) {
                cm = instance;
                instance = null;
            }
            if (null != cm) {
                cm.memoryHandler.clear();
                Iterator<Map.Entry<PoolMode, BufferPool>> iterator = cm.poolMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    iterator.next().getValue().clear();
                }
                cm = null;
            }
        }
    }

    public long allocateRead(long size) {
        return memoryHandler.allocateRead(size);
    }

    public long allocateEdit(long address, long oldSize, long newSize) {
        return memoryHandler.allocateEdit(address, oldSize, newSize);
    }

    public long allocateWrite(long address, long oldSize, long newSize) {
        return memoryHandler.allocateWrite(address, oldSize, newSize);
    }

    private MemoryHandler.GcCallBack createGCCallBack() {
        return new MemoryHandler.GcCallBack() {
            @Override
            public boolean gc() {
                Iterator<Map.Entry<PoolMode, BufferPool>> iterator = poolMap.entrySet().iterator();
                boolean result = false;
                while (iterator.hasNext()) {
                    BufferPool pool = iterator.next().getValue();
                    AbstractBuffer buffer = (AbstractBuffer) pool.poll();
                    if (null != buffer) {
                        buffer.closeWithOutSync();
                        Reference<? extends Buffer> ref = null;
                        while (null != (ref = referenceQueue.poll())) {
                            synchronized (ref) {
                                ref.clear();
                                ref = null;
                            }
                        }
                        result = true;
                        break;
                    }
                }
                System.gc();
                return result;
            }

            @Override
            public void forceGC() {
                Iterator<Map.Entry<PoolMode, BufferPool>> iterator = poolMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    BufferPool pool = iterator.next().getValue();
                    List<AbstractBuffer> list = pool.pollAllCleanable();
                    if (!list.isEmpty()) {
                        for (AbstractBuffer buffer : list) {
                            buffer.closeWithOutSync();
                            Reference<? extends Buffer> ref = null;
                            while (null != (ref = referenceQueue.poll())) {
                                synchronized (ref) {
                                    ref.clear();
                                    ref = null;
                                }
                            }
                        }
                        break;
                    }
                }
                System.gc();
            }
        };
    }

    public <B extends AbstractBuffer> B getBuffer(PoolMode mode, URI uri) {
        synchronized (this) {
            BufferPool<B> pool = poolMap.get(mode);
            if (null == pool) {
                pool = new BufferPool(mode, referenceQueue);
                poolMap.put(mode, pool);
            }
            return pool.getBuffer(uri);
        }
    }

    public void registerBuffer(PoolMode mode, Buffer buffer) {
        if (mode.isAssignableFrom(buffer.getClass())) {
            if (maxExistsBuffer.get() > 0) {
                maxExistsBuffer.decrementAndGet();
                poolMap.get(mode).registerBuffer(buffer);
            } else {
                if (limitCount.incrementAndGet() == MAX_AUTO_FREE_COUNT) {
                    maxExistsBuffer.set(MAX_AUTO_FREE_COUNT);
                    limitCount.set(0);
                }
                callBack.forceGC();
                registerBuffer(mode, buffer);
            }

        }
    }

    public void resetTimer(long t) {
        Iterator<Map.Entry<PoolMode, BufferPool>> iterator = poolMap.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().resetTimer(t);
        }
    }

    public long getCurrentMemorySize() {
        return memoryHandler.getReadSize() + memoryHandler.getWriteSize();
    }

    public long getReadSize() {
        return memoryHandler.getReadSize();
    }

    public long getWriteSize() {
        return memoryHandler.getWriteSize();
    }

    public long getReadWaitCount() {
        return memoryHandler.getReadWaitCount();
    }

    public long getWriteWaitCount() {
        return memoryHandler.getWriteWaitCount();
    }

    public void removeBuffer(PoolMode mode, AbstractBuffer buffer) {
        poolMap.get(mode).remove(buffer);
    }

    public void returnMemory(Buffer buffer, BufferPrivilege bufferPrivilege, boolean positive) {
        memoryHandler.returnMemory(buffer, bufferPrivilege, positive);
        if (!positive) {
            maxExistsBuffer.incrementAndGet();
        }
    }


}
