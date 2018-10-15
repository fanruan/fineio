package com.fineio.cache;

/**
 * Created by daniel on 2017/3/2.
 */
public enum LEVEL {
    //Read的释放优先级高，优先释放
    READ,
    //EDIT释放优先级低，不优先释放
    EDIT,
    //WRITE是不允许释放并且控制最大占用空间
    WRITE
}
