package com.fineio.v21.cache;

import com.fineio.base.Bits;
import com.fineio.cache.CacheObject;
import com.fineio.io.Buffer;
import com.fineio.io.file.ReadIOFile;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.manager.manager.MemoryManager;
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
public class CacheManager {

    private ConcurrentMap<URI, Buffer> buffers = new ConcurrentHashMap<URI, Buffer>();
    private ConcurrentMap<URI, CacheObject<ReadIOFile>> files = new ConcurrentHashMap<URI, CacheObject<ReadIOFile>>();
    private ConcurrentMap<URI, Object> lockMap = new ConcurrentHashMap<URI, Object>();
    private ConcurrentMap<URI, byte[]> heads = new ConcurrentHashMap<URI, byte[]>();

    private CacheManager() {
        MemoryManager.INSTANCE.registerCleaner(new MemoryManager.Cleaner() {
            @Override
            public boolean clean() {
                return CacheManager.this.closeTimeout();
            }

            @Override
            public boolean cleanAllCleanable() {
                if (!clean()) {
                    Iterator<Map.Entry<URI, Buffer>> iterator = buffers.entrySet().iterator();
                    while (iterator.hasNext() && files.isEmpty()) {
                        Map.Entry<URI, Buffer> next = iterator.next();
                        synchronized (getURILock(next.getKey())) {
                            next.getValue().close();
                            iterator.remove();
                        }
                    }
                    return true;
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
            Buffer remove = buffers.remove(URI.create(path.endsWith("/") ? path + i : path + "/" + i));
            if (release && null != remove) {
                synchronized (getURILock(remove.getUri())) {
                    remove.close();
                }
            }
        }
    }

    public void close(final Buffer buf) {
        if (null != buf) {
            SingletonHolder.EXEC.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    FineIOLoggers.getLogger().debug(String.format("Buf %s close. Release %d", buf.getUri().getPath(), buf.getMemorySize()));
                    buf.close();
                    return null;
                }
            });
        }
    }

    private boolean closeTimeout() {
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
                    }
                }
            }
        }
        return false;
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
        private final static ExecutorService EXEC = FineIOThreadPoolExecutor.newInstance(Runtime.getRuntime().availableProcessors(), "fineio-cache-event-dispatcher");

    }
}
