package com.fineio.cache;

/**
 *
 * @author daniel
 * @date 2017/3/2
 */
public enum BufferPrivilege {
    //Read的释放优先级高，优先释放
    READABLE,
    //EDIT释放优先级低，不优先释放
    EDITABLE,
    //WRITE是不允许释放并且控制最大占用空间
    WRITABLE,

    CLEANABLE
}
