package com.fineio.io;

import java.io.InputStream;

/**
 * Created by daniel on 2017/2/20.
 */
public interface Buffer {

    /**
     * 是否写全信息
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
}
