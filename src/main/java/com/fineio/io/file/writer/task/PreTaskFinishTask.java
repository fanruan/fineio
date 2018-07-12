package com.fineio.io.file.writer.task;

/**
 * @author yee
 * @date 2018/7/12
 */
public class PreTaskFinishTask implements Task {

    private Runnable runnable;

    public PreTaskFinishTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() throws Exception {
        runnable.run();
    }
}
