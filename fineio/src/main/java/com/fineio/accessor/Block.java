package com.fineio.accessor;

/**
 * @author yee
 */
public interface Block {

    /**
     * 获取Path
     *
     * @return
     */
    String getPath();

    /**
     * 获取Name
     *
     * @return
     */
    String getName();

    Block clone();
}