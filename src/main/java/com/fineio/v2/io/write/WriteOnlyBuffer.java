package com.fineio.v2.io.write;

import com.fineio.v2.io.Buffer;

/**
 * @author yee
 * @date 2018/5/30
 */
public interface WriteOnlyBuffer extends Buffer {
    /**
     * 是否写全信息
     *
     * @return
     */
    boolean full();

    /**
     * 写的接口
     */
    void write();

    /**
     * 同步写文件并关闭 force之后不允许访问
     */
    void force();

    void forceAndClear();

    boolean hasChanged();

    boolean needClear();
}
