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

/**
 * Created by daniel on 2017/2/23.
 */
public class JobContainer {
    private Queue<JobAssist> jobs = new ConcurrentLinkedQueue<JobAssist>();
    private Map<BufferKey, JobAssist> watchMap = new ConcurrentHashMap<BufferKey, JobAssist>();

    private Lock lock = new ReentrantLock();


    public boolean put(JobAssist job) {
        try {
            lock.lock();
            if (!watchMap.containsKey(job.getKey())) {
                addJob(job);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return jobs.isEmpty();
    }

    public JobAssist get() {
        if(isEmpty()){
            return null;
        }
        try {
            lock.lock();
            JobAssist jobAssist = jobs.poll();
            watchMap.remove(jobAssist.getKey());
            return jobAssist;
        } finally {
            lock.unlock();
        }
    }


    public void waitJob(JobAssist jobAssist) {
        waitJob(jobAssist, null);
    }


    public void waitJob(JobAssist jobAssist, Job callbackJob) {
        if(jobAssist == null){
            return;
        }
        JobAssist job = null;
        lock.lock();
        job =  watchMap.get(jobAssist.getKey());
        if(job == null){
            job = jobAssist;
            addJob(job);
            if(callbackJob != null) {
                callbackJob.doJob();
            }
        }
        synchronized (job){
            try {
                //这里unlock是为了避免addJob之后直接被get方法夺走从而导致wait的对象已经被拿去执行
                lock.unlock();
                job.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    private void addJob(JobAssist job) {
        jobs.add(job);
        watchMap.put(job.getKey(), job);
    }
}
