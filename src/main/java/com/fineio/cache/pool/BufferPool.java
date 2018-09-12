package com.fineio.cache.pool;

import com.fineio.cache.BufferPrivilege;
import com.fineio.exception.FileCloseException;
import com.fineio.io.AbstractBuffer;
import com.fineio.thread.FineIOExecutors;

import java.lang.ref.ReferenceQueue;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/5/31
 */
public class BufferPool<Buffer extends com.fineio.io.Buffer> {
    /**
     * 默认10分钟清理超时buffer
     */
    private static final long DEFAULT_TIMER_TIME = 600000;
    private static final int ACTIVE_PERCENT = 10;
    private volatile long timeout = DEFAULT_TIMER_TIME;
    private PooledBufferMap<Buffer> map;
    private PoolMode mode;

    private ScheduledExecutorService timeoutService;
    private ScheduledExecutorService activeService;

    public BufferPool(PoolMode mode, ReferenceQueue<Buffer> referenceQueue) {
        this.mode = mode;
        initTimeoutService();
        initActiveService();
        map = new PooledBufferMap<Buffer>(referenceQueue);
    }

    private void initTimeoutService() {
        timeoutService = FineIOExecutors.newScheduledExecutorService(1, "FineIOCleanTimer-" + mode.name());
        timeoutService.scheduleAtFixedRate(createTimeoutTask(), timeout, timeout, TimeUnit.MILLISECONDS);
    }

    private void initActiveService() {
        activeService = FineIOExecutors.newScheduledExecutorService(1, "FineIOBufferActiveTimer_" + mode.name());
        activeService.scheduleAtFixedRate(createBufferActiveTask(), timeout / ACTIVE_PERCENT, timeout / ACTIVE_PERCENT, TimeUnit.MILLISECONDS);
    }

    public void registerBuffer(Buffer buffer) {
        map.put(buffer);
    }

    public Buffer getBuffer(URI uri) {
        return map.get(uri);
    }

    private TimerTask createBufferActiveTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    resetAccess(map);
                    //10秒内有访问全部命中
                    Thread.sleep(10000);
                    activeAccess(map);
                } catch (Throwable e) {
                    //doNothing
                }
            }


            private void activeAccess(PooledBufferMap<Buffer> map) {
                Iterator<Buffer> iterator = map.iterator();
                while (iterator.hasNext()) {
                    Buffer buffer = iterator.next();
                    if (buffer != null && buffer.recentAccess()) {
                        try {
                            map.updateBuffer(buffer);
                        } catch (FileCloseException e) {
                            //file closed
                        }
                    }
                }
            }

            private void resetAccess(PooledBufferMap<Buffer> map) {
                Iterator<Buffer> iterator = map.iterator();
                while (iterator.hasNext()) {
                    Buffer buffer = iterator.next();
                    if (buffer != null) {
                        buffer.resetAccess();
                    }
                }
            }
        };
    }

    //定时清超时任务的task
    private TimerTask createTimeoutTask() {
        return new TimerTask() {
            @Override
            public void run() {
                removeTimeout();
            }

            private void removeTimeout() {
                Set<Buffer> cleanable = getIdleBuffer();
                for (Buffer buffer : cleanable) {
                    ((AbstractBuffer) buffer).unReference();
                }
            }
        };
    }

    /**
     * 重设超时时间
     *
     * @param t
     */
    public synchronized void resetTimer(long t) {
        timeout = t;
        if (timeoutService != null) {
            timeoutService.shutdown();
        }
        initTimeoutService();
        if (activeService != null) {
            activeService.shutdown();
        }
        initActiveService();
    }

    public void remove(Buffer buffer) {
        map.remove(buffer);
    }

    public Set<Buffer> getIdleBuffer() {
        Iterator<Buffer> iterator = map.iterator();
        HashSet<Buffer> buffers = new HashSet<Buffer>();
        while (iterator.hasNext()) {
            Buffer buffer = iterator.next();
            if (buffer != null && buffer.getBufferPrivilege().compareTo(BufferPrivilege.EDITABLE) < 0) {
                if (map.getIdle(buffer) > timeout) {
                    buffers.add(buffer);
                }
            }
        }
        return buffers;
    }

    public void clear() {
        if (null != timeoutService) {
            timeoutService.shutdown();
        }
        if (null != activeService) {
            activeService.shutdown();
        }
        Iterator<Buffer> iterator = map.iterator();
        while (iterator.hasNext()) {
            Buffer buffer = iterator.next();
            if (buffer != null) {
                buffer.close();
            }
        }
    }

    public Buffer poll() {
        return map.poll();
    }

    public List<Buffer> pollAllCleanable() {
        return map.pollAllCleanable();
    }
}
