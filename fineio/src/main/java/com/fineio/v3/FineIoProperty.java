package com.fineio.v3;

/**
 * @author anchore
 * @date 2019/9/12
 */
public class FineIoProperty<T> {
    public static final FineIoProperty<Long> READ_MEM_LIMIT = ofSystemLong("fineio.read_mem_limit", 2D);
    public static final FineIoProperty<Long> WRITE_MEM_LIMIT = ofSystemLong("fineio.write_mem_limit", 1D);
    public static final FineIoProperty<Long> CACHE_MEM_LIMIT = ofSystemLong("fineio.cache_mem_limit", 1D);

    private final String name;
    private final double defaultValue;
    private double value;

    private FineIoProperty(String name, double defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        try {
            final String apply = System.getProperty(name);
            if (apply != null) {
                value = Double.parseDouble(apply);
            } else {
                value = defaultValue;
            }
        } catch (Exception ignore) {
            value = defaultValue;
        }
    }

    private static FineIoProperty<Long> ofSystemLong(String name, Double defaultValue) {
        // 通过vm option -Dname=value引入
        return new FineIoProperty<>(name, defaultValue);
    }

    public String getName() {
        return name;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public double getValue() {
        return value;
    }
}