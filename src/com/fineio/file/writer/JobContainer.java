package com.fineio.file.writer;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by daniel on 2017/2/23.
 */
public class JobContainer {
    private Queue<JobAssist> jobs = new ConcurrentLinkedQueue<JobAssist>();
    private Map<SyncKey, JobAssist> watchMap = new ConcurrentHashMap<SyncKey, JobAssist>();


    public boolean put(JobAssist job) {
        synchronized (this){
            if(!watchMap.containsKey(job.getKey())) {
                jobs.add(job);
                watchMap.put(job.getKey(), job);
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return jobs.isEmpty();
    }

    public JobAssist get() {
        if(isEmpty()){
            return null;
        }
        synchronized (this){
            JobAssist jobAssist = jobs.poll();
            watchMap.remove(jobAssist.getKey());
            return  jobAssist;
        }
    }



}
