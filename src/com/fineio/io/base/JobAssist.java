package com.fineio.io.base;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/23.
 */
public class JobAssist {


    private BufferKey key;
    private Job job;

    public JobAssist(BufferKey key, Job job) {
        this.key = key;
        this.job = job;
    }

    //for test
    public JobAssist(Connector connector, FileBlock block, Job job) {
        this.key = new BufferKey(connector, block);
        this.job = job;
    }

    public BufferKey getKey() {
        return key;
    }

    public void doJob() {
        job.doJob();
    }
}
