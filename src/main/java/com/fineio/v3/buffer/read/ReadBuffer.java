package com.fineio.v3.buffer.read;

import com.fineio.v3.buffer.Buffer;

/**
 * @author yee
 */
public interface ReadBuffer extends Buffer {

    /**
     * 加载持久化数据
     */
    void load();

}