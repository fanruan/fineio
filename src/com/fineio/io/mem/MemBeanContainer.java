package com.fineio.io.mem;


import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2017/12/04
 */
public class MemBeanContainer {
    private static MemBeanContainer container;
    private ConcurrentHashMap<URI, MemBean> beans = new ConcurrentHashMap<URI, MemBean>();

    public static MemBeanContainer getContainer() {
        if (null == container) {
            synchronized (MemBeanContainer.class) {
                if (null == container) {
                    container = new MemBeanContainer();
                }
            }
        }
        return container;
    }

    public void registerMemBean(MemBean bean) {
        beans.put(bean.getUri(), bean);
    }

    public void remove(URI key) {
        beans.remove(key);
    }

    public boolean contains(URI key) {
        return beans.containsKey(key);
    }

    public MemBean get(URI key) {
        return beans.get(key);
    }

    public synchronized void clear() {
        beans.clear();
    }
}
