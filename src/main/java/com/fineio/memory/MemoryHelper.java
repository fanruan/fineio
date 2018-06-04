package com.fineio.memory;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

/**
 * Created by daniel on 2017/2/13.
 */
public final class MemoryHelper {

    static long getMaxMemory() {
        try {
            OperatingSystemMXBean mb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            long max = mb.getTotalPhysicalMemorySize();
            return max - Runtime.getRuntime().maxMemory();
        } catch (Throwable e){
            //如果发生异常则使用xmx值
           return Runtime.getRuntime().maxMemory();
        }
    }

    static long getFreeMemory() {
        try {
            OperatingSystemMXBean mb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            return mb.getFreePhysicalMemorySize();
        } catch (Throwable e){
            //如果发生异常则使用xmx值
            return Runtime.getRuntime().maxMemory();
        }
    }
}
