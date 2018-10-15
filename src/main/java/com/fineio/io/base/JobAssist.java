package com.fineio.io.base;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JobAssist {
    private BufferKey key;
    private Job job;
    private volatile boolean finished;
    private List<JobAssist> linkedJob;

    public JobAssist(final BufferKey key, final Job job) {
        this.finished = false;
        this.key = key;
        this.job = job;
    }

    public JobAssist(final Connector connector, final FileBlock fileBlock, final Job job) {
        this.finished = false;
        this.key = new BufferKey(connector, fileBlock);
        this.job = job;
    }

    public BufferKey getKey() {
        return this.key;
    }

    public void doJob() {
        this.job.doJob();
    }

    public void registerLinkJob(final JobAssist jobAssist) {
        synchronized (this) {
            if (this.finished) {
                jobAssist.notifyJobs();
            }
            if (this.linkedJob == null) {
                this.linkedJob = new ArrayList<JobAssist>();
            }
            this.linkedJob.add(jobAssist);
        }
    }

    public void notifyJobs() {
        synchronized (this) {
            this.notifyAll();
            if (this.linkedJob != null) {
                final Iterator<JobAssist> iterator = this.linkedJob.iterator();
                while (iterator.hasNext()) {
                    iterator.next().notifyJobs();
                }
            }
            this.finished = true;
        }
    }
}
