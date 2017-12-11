package com.fineio.io.file.writer;

import com.fineio.base.Worker;
import com.fineio.io.base.BufferKey;


/**
 * @author yee
 * @date 2017/12/04
 */
public abstract class SyncTask implements Worker {

    private BufferKey bufferKey;
    private volatile boolean access;

    final void access(){
        if(!access){
            access = true;
        }
    }

    public SyncTask(BufferKey bufferKey) {
        this.bufferKey = bufferKey;
    }

    /**
     * 是否刚被访问过
     * @return
     */
    public boolean recentAccess(){
        return access;
    }

    /**
     * 重置access状态
     */
    public void resetAccess() {
        access = false;
    }

    public BufferKey getBufferKey() {
        return bufferKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyncTask syncTask = (SyncTask) o;

        return bufferKey.equals(syncTask.bufferKey);
    }

    public void setBufferKey(BufferKey bufferKey) {
        this.bufferKey = bufferKey;
    }

    @Override
    public int hashCode() {
        return bufferKey.hashCode();
    }

    public final static SyncTask taskKey(BufferKey key) {
        return new SyncTask(key) {
            @Override
            public void work() {

            }
        };
    }
}
