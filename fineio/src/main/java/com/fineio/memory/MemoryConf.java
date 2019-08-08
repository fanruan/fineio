package com.fineio.memory;


import com.fineio.exception.MemorySetException;

/**
 * Created by daniel on 2017/2/13.
 */
public final class MemoryConf {

    //最小1G//哪个破电脑没2g内存的
    private static final long min_load_size = 1L << 30;
    //临时变量
    private static long max_load_size_temp;
    //final值
    private final static long max_load_size = max_load_size_temp;
    private static volatile long max_size = max_load_size;

    static {
        try {
            max_load_size_temp = Math.min(MemoryHelper.getMaxMemory(), MemoryHelper.getFreeMemory());
        } catch (Throwable e) {
            //这里是防止MemoryHelper加载不到sun的jdk
            max_load_size_temp = Runtime.getRuntime().maxMemory();
        }
        max_load_size_temp = Math.max(min_load_size, max_load_size_temp);
    }

    /**
     * 获取设置的可以用对外内存大小
     * 默认值是max_load_size
     *
     * @return
     * @ses MemoryConf.max_load_size
     * 大于1G，小于物理内存-Xmx的值
     * 默认为物理内存减去Xmx的值
     * 如果获取不到物理内存则使用Xmx的值
     * 配置方法@see setTotalMemSize();
     */
    public final static long getTotalMemSize() {
        return max_size;
    }

    /**
     * 设置内存大小，值区间必须在 getMaxMemSizeForSet() 与 getMinMemSizeForSet()之间
     * 否则抛出异常
     *
     * @param size
     * @throws MemorySetException
     */
    public final static void setTotalMemSize(long size) throws MemorySetException {
        if (size > getMinMemSizeForSet() && size < getMaxMemSizeForSet()) {
            MemoryConf.max_size = size;
        } else {
            throw new MemorySetException("memory size must between: " + getMinMemSizeForSet() + " and " + getMaxMemSizeForSet() + " current : " + max_size);
        }
    }

    /**
     * freeMemory是设置的最大值与系统可用内存的最小值
     *
     * @return
     */
    public final static long getFreeMemory() {
        return Math.min(max_size, MemoryHelper.getFreeMemory());
    }

    public final static long getMaxMemSizeForSet() {
        return max_load_size;
    }

    public final static long getMinMemSizeForSet() {
        return min_load_size;
    }

}
