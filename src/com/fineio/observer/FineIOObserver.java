package com.fineio.observer;

/**
 * @author yee
 * @date 2018/4/15
 */
public interface FineIOObserver<T extends FineIOObservable> {
    void update(T observable);
}
