package com.fineio.memory.manager.obj;

/**
 * @author yee
 * @date 2018/9/19
 */
public class SyncObject {
    private final static int MAX_COUNT = 512;

    protected void beforeStatusChange() {
        //等待1024计算更加危险的状态控制
        int count = 0;
        while (count++ < MAX_COUNT) {
        }
    }

}
