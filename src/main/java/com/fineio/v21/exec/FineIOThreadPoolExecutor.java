package com.fineio.v21.exec;

import com.fineio.thread.FineIOThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @version 1.1
 * Created by yee on 2019-09-06
 */
public class FineIOThreadPoolExecutor extends ThreadPoolExecutor {
    private FineIOThreadPoolExecutor(int poolSize, String namePrefix) {
        super(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        setThreadFactory(new FineIOThreadFactory(namePrefix));
    }

    public static ExecutorService newInstance(int poolSize, String namePrefix) {
        return new FineIOThreadPoolExecutor(poolSize, namePrefix);
    }
}
