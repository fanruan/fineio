package com.fineio.cache;

/**
 * Created by daniel on 2017/3/6.
 */
public interface Allocator {

    long getChangeSize();

    long allocate();

}
