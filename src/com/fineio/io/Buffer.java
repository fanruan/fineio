package com.fineio.io;

import com.fineio.cache.LEVEL;
import com.fineio.io.base.BufferKey;

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

    /*
    * 直接抛弃内存不写文件
     */
    void closeWithOutSync();


    /**
     * 释放的方法
     * 注意 读和编辑可以随意调用clear
     * 写如果clear那么就不能再访问了
     */
    void clear();


    /**
     * 获取LEVEL
     * @return
     */
    LEVEL getLevel();


    /**
     * 是否被访问状态
     * @return
     */
    boolean recentAccess();
    /**
     * 重置access
     */
    void resetAccess();

    /**
     * 获取分配的内存大小
     * @return
     */
    int getAllocateSize();

    /**
     * 获取byte大小
     * @return
     */
    int getByteSize();


    /**
     * 获取类型可用长度
     * @return
     */
    int getLength();
}
