package com.fineio.io;

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


    /**
     * 释放的方法
     * 注意 读和编辑可以随意调用clear
     * 写如果clear那么就不能再访问了
     */
    void clear();
}
