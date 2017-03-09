package com.fineio.monitor;

import com.fineio.base.Worker;
import com.fineio.cache.CacheManager;
import com.fineio.exception.MemorySetException;
import com.fineio.memory.MemoryConf;

/**
 * Created by daniel on 2017/3/9.
 */
public class MonitorUtils {


    public static void resetMemory(Worker worker, int reduce_size) throws  MemorySetException{
        synchronized (MonitorUtils.class){
            try{
                worker.work();
            } catch (OutOfMemoryError e) {
                MemoryConf.setTotalMemSize(CacheManager.getInstance().getCurrentMemorySize() - reduce_size);
                resetMemory(worker);
            }
        }
    }


    private static void resetMemory(Worker worker){
        try {
            worker.work();
        } catch (OutOfMemoryError error) {
            resetMemory(worker);
        }
    }

}
