package com.fineio.v1.cache;


import com.fineio.thread.FineIOExecutors;

import java.lang.ref.ReferenceQueue;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by daniel on 2017/3/7.
 */
public class CacheKeyLinkedMap<K, T> {
    //链表只是控制释放不一定保存了所有的值
    private CacheObject<T> head;
    private CacheObject<T> foot;
    private ReferenceQueue<T> referenceQueue;
    private LinkedBlockingQueue<K> updateQueue;
    private ExecutorService updateThread;
    private Map<K, CacheObject<T>> indexMap = new ConcurrentHashMap<K, CacheObject<T>>();

    public CacheKeyLinkedMap(ReferenceQueue<T> referenceQueue) {
        this();
        this.referenceQueue = referenceQueue;
    }

    public CacheKeyLinkedMap() {
        this.updateQueue = new LinkedBlockingQueue<K>();
        this.updateThread = FineIOExecutors.newSingleThreadExecutor(CacheKeyLinkedMap.class);
        this.updateThread.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        K key = updateQueue.take();
                        CacheObject<T> co = indexMap.get(key);
                        if (co != null) {
                            co.updateTime();
                            doChange(co);
                        }
                    } catch (InterruptedException ignore) {
                    }
                }
            }
        });
    }

    public boolean update(K t) {
        synchronized (this) {
            updateQueue.offer(t);
        }
        return false;
    }

    public long getIdle(K t) {
        CacheObject<T> co = indexMap.get(t);
        if (co != null) {
            return co.getIdle();
        }
        return 0;
    }

    public Iterator<K> iterator() {
        return indexMap.keySet().iterator();
    }

    public T get(K key, boolean update) {
        CacheObject<T> co = indexMap.get(key);
        if (null != co) {
            if (update) {
                update(key);
            }
            return co.get();
        }
        return null;
    }

    /**
     * update只有在已经分配内存的情况下会进行执行
     *
     * @param co
     */
    private void doChange(CacheObject<T> co) {
        if (co.getLast() == null && co.getNext() == null) {
            if (co != head) {
                co.setNext(head);
                setHead(co);
                if (foot == null) {
                    setFoot(co);
                }
            }
        } else if (head != co) {
            //此情况下不存在co.getLast为null的情况如果getLast是null 那么必然head == co
            co.getLast().setNext(co.getNext());
            if (co == foot) {
                setFoot(co.getLast());
            }
            co.setNext(head);
            setHead(co);
        }
    }

    private void setHead(CacheObject<T> v) {
        head = v;
        if (v != null) {
            head.setLast(null);
        }
    }

    private void setFoot(CacheObject<T> v) {
        foot = v;
        if (v != null) {
            foot.setNext(null);
        }
    }

    /**
     * put在注册的时候使用并不会将对象放到已经申请内存的队列
     *
     * @param t
     */
    public void put(K k, T t) {
        synchronized (this) {
            CacheObject<T> co = indexMap.get(k);
            if (co == null) {
                co = new CacheObject<T>(t, referenceQueue);
                indexMap.put(k, co);
            } else {
                co.updateTime();
            }
        }
    }


    public void remove(K t, boolean remove) {
        synchronized (this) {
            CacheObject<T> co = indexMap.get(t);
            if (co == null) {
                return;
            }
            if (co.getNext() == null && co.getLast() == null) {
                //如果是head 那么该情况下foot也是 co
                if (head == co) {
                    setFoot(null);
                    setHead(null);
                }
            } else {
                if (head == co) {
                    //该情况getNext肯定非空
                    setHead(co.getNext());
                } else {
                    //head 不是 co的情况 last肯定非空
                    co.getLast().setNext(co.getNext());
                    if (co == foot) {
                        setFoot(co.getLast());
                    }
                }
                co.setLast(null);
                co.setNext(null);
            }
            //是否彻底删除掉
            if (remove) {
                indexMap.remove(t);
            }
        }
    }

    public T peek() {
        synchronized (this) {
            return foot == null ? null : foot.get();
        }
    }

    public T poll() {
        synchronized (this) {
            if (foot != null) {
                CacheObject<T> res = foot;
                if (foot.getLast() != null) {
                    foot.getLast().setNext(null);
                } else {
                    setHead(null);
                }
                setFoot(foot.getLast());
                res.setLast(null);
                res.setNext(null);
                return res.get();
            }
            return null;
        }
    }

    public boolean contains(K buffer) {
        return indexMap.containsKey(buffer);
    }

    public final int size() {
        return indexMap.size();
    }
}
