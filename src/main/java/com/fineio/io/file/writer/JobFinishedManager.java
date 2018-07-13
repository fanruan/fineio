package com.fineio.io.file.writer;

import com.fineio.io.file.writer.task.DoneTaskKey;
import com.fineio.io.file.writer.task.FinishOneTaskKey;
import com.fineio.io.file.writer.task.JobFutureTask;
import com.fineio.io.file.writer.task.Pair;
import com.fineio.io.file.writer.task.TaskKey;
import com.fineio.logger.FineIOLoggers;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author yee
 * @date 2018/7/12
 */
public final class JobFinishedManager {
    private ExecutorService consume = Executors.newSingleThreadExecutor();
    private ExecutorCompletionService<URI> service = new ExecutorCompletionService<URI>(Executors.newCachedThreadPool());
    private volatile static JobFinishedManager instance;
    public TaskMap map = new TaskMap();

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
                        Future<URI> uri = service.take();
                        map.remove(new FinishOneTaskKey(uri.get()));
                        if (!map.isEmpty() && map.firstKey().getType() == TaskKey.KeyType.DONE) {
                            ((Runnable) map.poll()).run();
                        }
                    }
                } catch (Exception e) {
                    FineIOLoggers.getLogger().error(e);
                }
            }
        });
    }

    void submit(final Future<Pair<URI, Boolean>> future) {
        service.submit(new JobFutureTask(future));
    }

    public void addTask(URI uri) {
        map.addTask(uri);
    }

    public void finish(final Runnable runnable) {
        map.finish(runnable);
    }

    public class TaskMap {
        private ConcurrentHashMap<TaskKey, Object> map = new ConcurrentHashMap<TaskKey, Object>();
        private List<TaskKey> linkedList = Collections.synchronizedList(new LinkedList<TaskKey>());

        public void addTask(URI uri) {
            TaskKey key = new FinishOneTaskKey(uri);
            linkedList.add(key);
            map.put(key, uri);
        }

        public void finish(final Runnable runnable) {
            TaskKey key = new DoneTaskKey();
            linkedList.add(key);
            map.put(key, runnable);
        }

        public TaskKey firstKey() {
            try {
                return linkedList.get(0);
            } catch (Exception ignore) {
                return null;
            }
        }

        public Object poll() {
            TaskKey key = firstKey();
            if (null != key) {
                Object result = map.get(key);
                remove(key);
                return result;
            }
            return null;
        }

        public void remove(TaskKey key) {
            try {
                linkedList.remove(key);
                map.remove(key);
            } catch (Exception ignore) {
            }
        }

        public boolean isEmpty() {
            return linkedList.isEmpty() && map.isEmpty();
        }
    }
}
