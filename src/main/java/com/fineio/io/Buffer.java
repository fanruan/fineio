package com.fineio.io;

import com.fineio.cache.BufferPrivilege;

import java.net.URI;

/**
 * @author yee
 * @date 2018/5/30
 */
public interface Buffer {
    /**
     * 获取Buffer地址
     *
     * @return
     */
    long getAddress();

    /**
     * 最大可访问size
     *
     * @return
     */
    int getMaxSize();

    int getAllocateSize();

    boolean isDirect();

    int getOffset();

    void close();

//    clear接口不开放了
//    void clear();

    int getStatus();

    /**
     * 是否被访问状态
     *
     * @return
     */
    boolean recentAccess();

    /**
     * 重置access
     */
    void resetAccess();

    void closeWithOutSync();

    BufferPrivilege getBufferPrivilege();

    /**
     * 获取byte大小
     *
     * @return
     */
    int getByteSize();


    /**
     * 获取类型可用长度
     *
     * @return
     */
    int getLength();

    URI getUri();
}
