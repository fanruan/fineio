package com.fineio.v2.cache;

/**
 * Created by daniel on 2017/3/6.
 */
public interface Allocator {

    long getChangeSize();

    long allocate();

}
