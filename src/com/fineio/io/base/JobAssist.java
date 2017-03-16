package com.fineio.io.base;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 2017/2/23.
 */
public class JobAssist {


    private BufferKey key;
    private Job job;
    private volatile  boolean finished = false;

    private List<JobAssist> linkedJob;

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

    public void registerLinkJob(JobAssist jobAssist) {
        synchronized (this) {
            if(finished){
                jobAssist.notifyJobs();
            }
            if (linkedJob == null) {
                linkedJob = new ArrayList<JobAssist>();
            }
            linkedJob.add(jobAssist);
        }
    }

    public void notifyJobs(){
        synchronized (this){
            this.notifyAll();
            if(linkedJob != null){
                for(JobAssist assist : linkedJob){
                    assist.notifyJobs();
                }
            }
            finished = true;
        }
    }

}
