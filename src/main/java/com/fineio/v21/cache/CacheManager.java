package com.fineio.v21.cache;

import com.fineio.FineIoService;
import com.fineio.base.Bits;
import com.fineio.cache.CacheObject;
import com.fineio.io.Buffer;
import com.fineio.io.Level;
import com.fineio.io.file.ReadIOFile;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.storage.Connector;
import com.fineio.v21.exec.FineIOThreadPoolExecutor;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2019/9/12
 */
public class CacheManager implements FineIoService {

    private ConcurrentMap<URI, Buffer> buffers = new ConcurrentHashMap<URI, Buffer>();
    private ConcurrentMap<URI, CacheObject<ReadIOFile>> files = new ConcurrentHashMap<URI, CacheObject<ReadIOFile>>();
    private ConcurrentMap<URI, Object> lockMap = new ConcurrentHashMap<URI, Object>();
    private ConcurrentMap<URI, byte[]> heads = new ConcurrentHashMap<URI, byte[]>();

    private CacheManager() {
        MemoryManager.INSTANCE.registerCleaner(new MemoryManager.Cleaner() {
            @Override
            public synchronized boolean cleanTimeout() {
                return closeTimeout();
            }

            @Override
            public synchronized boolean clean(int maxCount) {
                if (!cleanTimeout()) {
                    final Iterator<Map.Entry<URI, Buffer>> iterator = buffers.entrySet().iterator();
                    boolean hasOne = iterator.hasNext();
                    while (iterator.hasNext()) {
                        Buffer buffer = iterator.next().getValue();
                        if (buffer.getLevel() == Level.READ){
                            buffer.close();
                            iterator.remove();
                            if (--maxCount <= 0){
                                return true;
                            }
                        }
                    }
                    return hasOne;
                }
                return false;
            }

            @Override
            public void cleanReadable() {

            }
        });
    }

    public static CacheManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void put(Buffer buf) {
        if (null != buf) {
            final URI uri = buf.getUri();
            synchronized (getURILock(uri)) {
                buffers.put(uri, buf);
            }
        }
    }

    public void putHead(URI uri, byte[] head) {
        heads.put(uri, head);
    }

    public Buffer get(URI uri, BufferCreator creator) {
        if (buffers.containsKey(uri)) {
            return buffers.get(uri);
        }
        synchronized (getURILock(uri)) {
            Buffer buf = buffers.get(uri);
            if (null == buf && null != (buf = creator.createBuffer())) {
                buffers.put(uri, buf);
            }
            return buf;
        }
    }

    public byte[] get(URI uri, HeadReader reader) throws IOException {
        if (heads.containsKey(uri)) {
            return heads.get(uri);
        }
        byte[] bytes = reader.readHead();
        heads.put(uri, bytes);
        return bytes;
    }

    public <B extends Buffer> ReadIOFile<B> get(URI uri, FileCreator<B> creator) {
        CacheObject<ReadIOFile> cache = files.get(uri);
        if (null != cache && null != cache.get()) {
            cache.updateTime();
            return cache.get();
        }
        synchronized (getURILock(uri)) {
            CacheObject<ReadIOFile> cacheObject = files.get(uri);
            if (null == cacheObject || null == cacheObject.get()) {
                final ReadIOFile<B> file = creator.createFile();
                if (file.isValid()) {
                    cacheObject = new CacheObject<ReadIOFile>(file);
                    files.put(uri, cacheObject);
                }
                return file;
            }
            cacheObject.updateTime();
            return cacheObject.get();
        }
    }

    private Object getURILock(URI uri) {
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

    public void removeBuffers(URI uri, int length, boolean release) {
        for (int i = 0; i < length; i++) {
            String path = uri.getPath();
            URI uri1 = URI.create(path.endsWith("/") ? path + i : path + "/" + i);
            if (buffers.containsKey(uri1) && buffers.get(uri1).getLevel() == Level.READ){
                Buffer remove = buffers.remove(uri1);
                if (release && null != remove) {
                    synchronized (getURILock(remove.getUri())) {
                        remove.close();
                    }
                }
            }
        }
    }

    public void close(final Buffer buf) {
        if (null != buf) {
            buf.close();
        }
    }

    private boolean closeTimeout() {
        boolean cleared = false;
        try {
            final Iterator<Map.Entry<URI, CacheObject<ReadIOFile>>> iterator = files.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<URI, CacheObject<ReadIOFile>> next = iterator.next();
                final CacheObject<ReadIOFile> cache = next.getValue();
                if (cache.getIdle() > TimeUnit.MINUTES.toMillis(5)) {
                    final ReadIOFile readIOFile = cache.get();
                    if (null == readIOFile) {
                        iterator.remove();
                        byte[] remove = heads.remove(next.getKey());
                        if (null != remove) {
                            int size = Bits.getInt(remove, 0);
                            removeBuffers(next.getKey(), size, true);
                        } else {
                            while (iterator.hasNext()) {
                                Iterator<Map.Entry<URI, Buffer>> mit = buffers.entrySet().iterator();
                                final Map.Entry<URI, Buffer> entry = mit.next();
                                if (entry.getKey().getPath().contains(next.getKey().getPath()) && entry.getValue().getLevel() == Level.READ) {
                                    entry.getValue().close();
                                    mit.remove();
                                }
                            }
                        }
                    } else {
                        readIOFile.close();
                    }
                    cleared = true;
                }
            }
        } catch (Exception e){
            FineIOLoggers.getLogger().error(e);
        }
        return cleared;
    }

    /**
     * 缓存一个空的cache的readfile的uri，释放的时候可以被超时释放，读的时候因为cache为null会再加载一次的
     * @param uri
     */
    public void updateFile(URI uri) {
        CacheObject<ReadIOFile> cache = files.get(uri);
        if (null != cache && null != cache.get()) {
            cache.updateTime();
            return;
        }
        synchronized (getURILock(uri)) {
            CacheObject<ReadIOFile> cacheObject = files.get(uri);
            if (null == cacheObject) {
                cacheObject = new CacheObject<ReadIOFile>(null);
                files.put(uri, cacheObject);
            }
            cacheObject.updateTime();
        }
    }

    public interface BufferCreator {
        Buffer createBuffer();
    }

    public interface FileCreator<B extends Buffer> {
        ReadIOFile<B> createFile();
    }

    public interface HeadReader {
        byte[] readHead() throws IOException;
    }

    private static class SingletonHolder {
        private static CacheManager INSTANCE = new CacheManager();

    }


    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
