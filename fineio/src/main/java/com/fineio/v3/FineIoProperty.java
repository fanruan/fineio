package com.fineio.v3;

import java.util.function.Function;

/**
 * @author anchore
 * @date 2019/9/12
 */
public class FineIoProperty<T> {
    public static final FineIoProperty<Long> READ_MEM_LIMIT = ofSystemLong("fineio.read_mem_limit", 2L);
    public static final FineIoProperty<Long> WRITE_MEM_LIMIT = ofSystemLong("fineio.write_mem_limit", 1L);
    public static final FineIoProperty<Long> CACHE_MEM_LIMIT = ofSystemLong("fineio.cache_mem_limit", 1L);

    private final String name;
    private final T defaultValue;
    private T value;

    private FineIoProperty(String name, T defaultValue, Function<String, String> valueGetter, Function<String, T> valueCalculator) {
        this.name = name;
        this.defaultValue = defaultValue;
        try {
            final T apply = valueCalculator.compose(valueGetter).apply(name);
            if (apply != null) {
                value = apply;
                return;
            }
        } catch (Exception ignore) {
        }
        value = defaultValue;
    }

    private static FineIoProperty<Long> ofSystemLong(String name, Long defaultValue) {
        // 通过vm option -Dname=value引入
        return new FineIoProperty<>(name, defaultValue, System::getProperty, Long::valueOf);
    }

    public String getName() {
        return name;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue() {
        return value;
    }
}