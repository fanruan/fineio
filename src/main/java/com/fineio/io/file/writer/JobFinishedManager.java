package com.fineio.io.file.writer;

import com.fineio.io.file.writer.task.DoneTaskKey;
import com.fineio.io.file.writer.task.FinishOneTaskKey;
import com.fineio.io.file.writer.task.Pair;
import com.fineio.io.file.writer.task.TaskKey;
import com.fineio.logger.FineIOLoggers;
import com.fineio.thread.FineIOExecutors;

import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.LockSupport;

/**
 * @author yee
 * @date 2018/7/12
 */
public final class JobFinishedManager {
    private ExecutorService consume = FineIOExecutors.newSingleThreadExecutor(JobFinishedManager.class);
    private volatile static JobFinishedManager instance;
    public TaskMap map = new TaskMap();
    private Semaphore semaphore = new Semaphore(1);

    public static JobFinishedManager getInstance() {
        if (instance == null) {
            synchronized (SyncManager.class) {
                if (instance == null) {
                    instance = new JobFinishedManager();
                }
            }
        }
        return instance;
    }

    private JobFinishedManager() {
        consume.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        while (!map.isEmpty() && map.firstKey().getType() == TaskKey.KeyType.DONE) {
                            ((Runnable) map.poll()).run();
                        }
                        semaphore.acquire();
                    }
                } catch (Exception e) {
                    FineIOLoggers.getLogger().error(e);
                }
            }
        });
    }

    void submit(final Future<Pair<URI, Boolean>> future) throws ExecutionException, InterruptedException {
        Pair<URI, Boolean> uri = future.get();
        map.remove(new FinishOneTaskKey(uri.getKey()));
        LockSupport.parkNanos(100 * 1000);
        semaphore.release();
    }

    public void addTask(URI uri) {
        map.addTask(uri);
    }

    public void finish(final Runnable runnable) {
        map.finish(runnable);
    }

    public class TaskMap {
        private ConcurrentLinkedQueue<Pair<TaskKey, Object>> queue = new ConcurrentLinkedQueue<Pair<TaskKey, Object>>();
//        private List<TaskKey> linkedList = Collections.synchronizedList(new ArrayList<TaskKey>());

        public void addTask(URI uri) {
            TaskKey key = new FinishOneTaskKey(uri);
            queue.add(new Pair<TaskKey, Object>(key, uri));
            semaphore.release();
        }

        public void finish(final Runnable runnable) {
            TaskKey key = new DoneTaskKey();
//            linkedList.add(key);
            queue.add(new Pair<TaskKey, Object>(key, runnable));
            semaphore.release();
        }

        public TaskKey firstKey() {
            try {
                Iterator<Pair<TaskKey, Object>> it = queue.iterator();
                if (it.hasNext()) {
                    return it.next().getKey();
                }
                return null;
            } catch (Exception ignore) {
                return null;
            }
        }

        public Object poll() {
            return queue.poll().getValue();
        }

        public void remove(TaskKey key) {
            try {
                Iterator<Pair<TaskKey, Object>> it = queue.iterator();
                while (it.hasNext()) {
                    Pair<TaskKey, Object> pair = it.next();
                    if (key.equals(pair.getKey())) {
                        it.remove();
                        return;
                    }
                }
            } catch (Exception ignore) {
            }
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }
    }
}
