package com.fineio.accessor.store;

import com.fineio.accessor.Block;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2019-05-22
 */
public interface IConnector<B extends Block> {
    /**
     * 读整块的方法
     *
     * @param file
     * @return
     */
    InputStream read(B file) throws IOException;

    /**
     * 写整快的方法，可以保证通一个块不被同时写
     *
     * @param file
     * @param inputStream
     */
    void write(B file, InputStream inputStream) throws IOException;

    /*
    输出byte[]
     */
    void write(B file, byte[] bytes) throws IOException;

    /**
     * 删除块
     *
     * @param block
     * @return
     */
    boolean delete(B block);

    /**
     * 写文件时单个块的最大size偏移量
     * 用1L << value 表示单个块的最大尺寸，不建议超过28 （256M） 不建议小于22 (4M)
     * 可以根据磁盘的读写能力控制这个值的大小介于12-31之间
     * 不支持小于12 4K
     * 不支持大于31 2G
     *
     * @return
     */
    byte getBlockOffset();

    /**
     * 文件是否存在
     *
     * @param block
     * @return
     */
    boolean exists(B block);
}
