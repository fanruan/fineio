package com.fineio.cache;

import com.fineio.cache.pool.BufferPool;
import com.fineio.cache.pool.PoolMode;
import com.fineio.io.AbstractBuffer;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * @author yee
 * @date 2018/5/31
 */
public class CacheManager {
    private volatile static CacheManager instance;

    private ConcurrentHashMap<PoolMode, BufferPool> poolMap;
    private MemoryHandler memoryHandler;
    public ReferenceQueue<? extends Buffer> referenceQueue;
    private AtomicLong maxExistsBuffer;


    private CacheManager() {
        memoryHandler = MemoryHandler.newInstance(createGCCallBack());
        long maxSize = MemoryHandler.getMaxMemory() >> 22;
        maxExistsBuffer = new AtomicLong(maxSize);
        referenceQueue = new ReferenceQueue<Buffer>();
        poolMap = new ConcurrentHashMap<PoolMode, BufferPool>();
        poolMap.put(PoolMode.BYTE, new BufferPool<ByteBuffer>(PoolMode.BYTE, (ReferenceQueue<ByteBuffer>) referenceQueue));
        poolMap.put(PoolMode.CHAR, new BufferPool<CharBuffer>(PoolMode.CHAR, (ReferenceQueue<CharBuffer>) referenceQueue));
        poolMap.put(PoolMode.SHORT, new BufferPool<ShortBuffer>(PoolMode.SHORT, (ReferenceQueue<ShortBuffer>) referenceQueue));
        poolMap.put(PoolMode.INT, new BufferPool<IntBuffer>(PoolMode.INT, (ReferenceQueue<IntBuffer>) referenceQueue));
        poolMap.put(PoolMode.LONG, new BufferPool<LongBuffer>(PoolMode.LONG, (ReferenceQueue<LongBuffer>) referenceQueue));
        poolMap.put(PoolMode.FLOAT, new BufferPool<FloatBuffer>(PoolMode.FLOAT, (ReferenceQueue<FloatBuffer>) referenceQueue));
        poolMap.put(PoolMode.DOUBLE, new BufferPool<DoubleBuffer>(PoolMode.DOUBLE, (ReferenceQueue<DoubleBuffer>) referenceQueue));
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
                    AbstractBuffer buffer = (AbstractBuffer) iterator.next().getValue().poll();
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
        };
    }

    public <B extends AbstractBuffer> B getBuffer(PoolMode mode, URI uri) {
        return (B) poolMap.get(mode).getBuffer(uri);
    }

    public void registerBuffer(PoolMode mode, Buffer buffer) {
        if (mode.isAssignableFrom(buffer.getClass())) {
            if (maxExistsBuffer.get() > 0) {
                maxExistsBuffer.decrementAndGet();
                poolMap.get(mode).registerBuffer(buffer);
            } else {
                for (int i = 0; i < 10; i++) {
                    memoryHandler.forceGC();
                    LockSupport.parkNanos(1 * 1000);
                }
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

    public void returnMemory(Buffer buffer, BufferPrivilege bufferPrivilege) {
        memoryHandler.returnMemory(buffer, bufferPrivilege);
        maxExistsBuffer.incrementAndGet();
    }


}
