package com.fineio.io.file.writer;

import com.fineio.io.base.BufferKey;
import com.fineio.io.base.Job;
import com.fineio.io.base.JobAssist;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JobContainer {
    private Queue<JobAssist> jobs;
    private Map<BufferKey, JobAssist> watchMap;
    private Lock lock;

    public JobContainer() {
        this.jobs = new ConcurrentLinkedQueue<JobAssist>();
        this.watchMap = new ConcurrentHashMap<BufferKey, JobAssist>();
        this.lock = new ReentrantLock();
    }

    public boolean put(final JobAssist jobAssist) {
        try {
            this.lock.lock();
            final JobAssist jobAssist2 = this.watchMap.get(jobAssist.getKey());
            if (jobAssist2 != null) {
                jobAssist2.registerLinkJob(jobAssist);
                return false;
            }
            this.addJob(jobAssist);
            return true;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean isEmpty() {
        return this.jobs.isEmpty();
    }

    public JobAssist get() {
        try {
            this.lock.lock();
            if (this.isEmpty()) {
                return null;
            }
            final JobAssist jobAssist = this.jobs.poll();
            this.watchMap.remove(jobAssist.getKey());
            return jobAssist;
        } finally {
            this.lock.unlock();
        }
    }

    public void waitJob(final JobAssist jobAssist) {
        this.waitJob(jobAssist, null);
    }

    public void waitJob(final JobAssist jobAssist, final Job job) {
        if (jobAssist == null) {
            return;
        }
        this.lock.lock();
        JobAssist jobAssist2 = this.watchMap.get(jobAssist.getKey());
        if (jobAssist2 == null) {
            jobAssist2 = jobAssist;
            this.addJob(jobAssist2);
            if (job != null) {
                job.doJob();
            }
        }
        synchronized (jobAssist2) {
            try {
                this.lock.unlock();
                jobAssist2.wait();
            } catch (InterruptedException ex) {
            }
        }
    }

    private void addJob(final JobAssist jobAssist) {
        this.jobs.add(jobAssist);
        this.watchMap.put(jobAssist.getKey(), jobAssist);
    }
}
