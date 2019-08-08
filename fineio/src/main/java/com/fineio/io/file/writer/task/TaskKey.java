package com.fineio.io.file.writer.task;

/**
 * @author yee
 * @date 2018/7/13
 */
public interface TaskKey {

    KeyType getType();

    enum KeyType {
        FINISHED, DONE
    }
}
