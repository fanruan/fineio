package com.fineio.v3.cache.demo;

/**
 * This class created on 2019/5/21
 *
 * @author Lucifer
 * @description
 */
public abstract class BaseDemo<T> {

    protected String createExpensiveGraph(String k) {
        return k + k;
    }
}
