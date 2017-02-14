package com.fineio.storage;

import com.fineio.file.FileBlock;

/**
 * Created by daniel on 2017/2/9.
 * 存储对接接口
 */
public interface Connector {

    /**
     * 读整块的方法
     * @param file
     * @return
     */
    byte[] read(FileBlock file);

    /**
     * 写整快的方法
     * @param file
     * @param v
     */
    void write(FileBlock file, byte[] v);

    /**
     * 写文件时单个块的最大size
     * 建议大于4M，并且是2的次方，建议提供用户配置自定义配置块的尺寸
     * 单位是byte建议
     * @return
     */
    long getBlockSize();

}
