package com.fineio.v21;

import java.util.function.Function;

/**
 * @author anchore
 * @date 2019/9/12
 */
public class FineIoProperty<T> {
    public static final FineIoProperty<Long> DIRECT_MEM_LIMIT = ofSystemLong("fineio.direct_mem_limit", -1L);

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
            }
        } catch (Exception ignore) {
            value = defaultValue;
        }
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