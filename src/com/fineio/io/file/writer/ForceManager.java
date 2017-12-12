package com.fineio.io.file.writer;

import com.fineio.cache.CacheLinkedMap;
import com.fineio.exception.FileCloseException;
import com.fineio.io.base.BufferKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author yee
 * @date 2017/12/04
 */
public final class ForceManager {
    private static volatile ForceManager instance;
    private CacheLinkedMap<SyncTask> tasks = new CacheLinkedMap<SyncTask>();
    private volatile List<BufferKey> flushed = Collections.synchronizedList(new ArrayList<BufferKey>());

    //超时的timer
    private Timer timer = new Timer();
    //activeTimer
    private Timer activeTimer = new Timer();

    private static final int ACTIVE_PERCENT = 10;
    private static final long DEFAULT_TIMEOUT = 600000;
    private volatile long timeout = DEFAULT_TIMEOUT;

    public static ForceManager getInstance() {
        if (null == instance) {
            synchronized (ForceManager.class) {
                if (null == instance) {
                    instance = new ForceManager();
                }
            }
        }
        return instance;
    }

    private ForceManager() {
        timer.schedule(createTimeoutTask(), timeout, timeout);
        activeTimer.schedule(createBufferActiveTask(), timeout / ACTIVE_PERCENT, timeout / ACTIVE_PERCENT);
    }

    public SyncTask registerBuffer(SyncTask task) {
        tasks.put(task);
        return task;
    }


    private TimerTask createTimeoutTask() {
        return new TimerTask() {
            @Override
            public void run() {
                syncTimeout();
            }

            private void syncTimeout() {
                Iterator<SyncTask> iterator = tasks.iterator();
                while (iterator.hasNext()) {
                    SyncTask task = iterator.next();
                    if (task != null) {
                        if (tasks.getIdle(task) > timeout) {
                            task.work();
                            tasks.remove(task, true);
                        }
                    }
                }
            }
        };
    }


    //定时激活buffer的task
    private TimerTask createBufferActiveTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    resetAccess();
                    //10秒内有访问全部命中
                    Thread.sleep(10000);
                    activeAccess();
                } catch (Throwable e) {
                    //doNothing
                }
            }


            private void activeAccess() {
                Iterator<SyncTask> iterator = tasks.iterator();
                while (iterator.hasNext()) {
                    SyncTask task = iterator.next();
                    if (task != null && task.recentAccess()) {
                        try {
                            tasks.update(task);
                        } catch (FileCloseException e) {
                            //file closed
                        }
                    }
                }
            }

            private void resetAccess() {
                Iterator<SyncTask> iterator = tasks.iterator();
                while (iterator.hasNext()) {
                    SyncTask task = iterator.next();
                    if (task != null) {
                        task.resetAccess();
                    }
                }
            }
        };
    }

    public void accessTask(SyncTask task) {
        if (task != null) {
            task.access();
            tasks.update(task);
        }
    }

    public boolean isFlushed(BufferKey bufferKey) {
        return flushed.contains(bufferKey);
    }

    public void flushed(BufferKey bufferKey) {
        flushed.add(bufferKey);
    }

    public void doSync(SyncTask task) {
        SyncTask syncTask = tasks.get(task);
        if (syncTask != null) {
            syncTask.work();
            tasks.remove(syncTask, true);
        }
    }

    /**
     * 重设超时时间
     * @param t
     */
    public synchronized void resetTimer(long t) {
        timeout = t;
        if(timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(createTimeoutTask(), timeout, timeout);
        if(activeTimer != null){
            activeTimer.cancel();
        }
        activeTimer = new Timer();
        activeTimer.schedule(createBufferActiveTask(), timeout/ACTIVE_PERCENT, timeout/ACTIVE_PERCENT);
    }
}
