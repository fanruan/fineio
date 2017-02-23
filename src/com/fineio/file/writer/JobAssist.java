package com.fineio.file.writer;

import com.fineio.file.FileBlock;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/23.
 */
public class JobAssist {


    private SyncKey key;
    private Job job;

    public JobAssist(Connector connector, FileBlock block, Job job) {
        this.key = new SyncKey(connector, block);
        this.job = job;
    }

    SyncKey getKey() {
        return key;
    }

    public void doJob() {
        job.doJob();
    }
}
