package com.fineio.cache.structure;

/**
 * @author yee
 * @date 2018/9/19
 */
public interface AutoRemoveCallBack<K, V> {
    void callback(K key, V value);
}
