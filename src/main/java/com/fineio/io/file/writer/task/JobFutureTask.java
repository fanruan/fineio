package com.fineio.io.file.writer.task;

import com.fineio.logger.FineIOLoggers;

import java.net.URI;
import java.util.concurrent.Future;

/**
 * @author yee
 * @date 2018/7/12
 */
public class JobFutureTask implements Task {

    private Future<Pair<URI, Boolean>> future;

    public JobFutureTask(Future<Pair<URI, Boolean>> future) {
        this.future = future;
    }

    @Override
    public void run() {
        try {
            Pair<URI, Boolean> value = future.get();
            URI uri = value.getKey();
            if (!value.getValue()) {
                FineIOLoggers.getLogger().error("write to " + uri + " error!");
            }
        } catch (Exception e) {
            FineIOLoggers.getLogger().error(e);
        }
    }
}
