package com.fineio.v3;

/**
 * @author anchore
 * @date 2019/9/12
 */
public class FineIoProperty<T> {
    public static final FineIoProperty<Long> READ_MEM_LIMIT = ofCalculatedSystemDouble("fineio.read_mem_limit", 2D, new ValueCalculator<Long>() {
        @Override
        public Long calculate(String value) {
            return (long) (Double.parseDouble(value) * (1 << 30));
        }
    });
    public static final FineIoProperty<Long> WRITE_MEM_LIMIT = ofCalculatedSystemDouble("fineio.write_mem_limit", 1D, new ValueCalculator<Long>() {
        @Override
        public Long calculate(String value) {
            return (long) (Double.parseDouble(value) * (1 << 30));
        }
    });
    public static final FineIoProperty<Long> CACHE_MEM_LIMIT = ofCalculatedSystemDouble("fineio.cache_mem_limit", 1D, new ValueCalculator<Long>() {
        @Override
        public Long calculate(String value) {
            return (long) (Double.parseDouble(value) * (1 << 30));
        }
    });

    private final String name;
    private final T defaultValue;
    private T value;

    private FineIoProperty(String name, T defaultValue, ValueGetter valueGetter, ValueCalculator<T> valueCalculator) {
        this.name = name;
        this.defaultValue = defaultValue;
        try {
            final T apply = valueCalculator.calculate(valueGetter.getValue(name));
            if (apply == null) {
                value = defaultValue;
            } else {
                value = apply;
            }
        } catch (Exception ignore) {
            value = defaultValue;
        }
    }

    private static FineIoProperty<Long> ofCalculatedSystemDouble(String name, Double defaultValue, ValueCalculator<Long> valueCalculator) {
        // 通过vm option -Dname=value引入
        return new FineIoProperty<>(name, valueCalculator.calculate(String.valueOf(defaultValue)), new ValueGetter() {
            @Override
            public String getValue(String name) {
                return System.getProperty(name);
            }
        }, valueCalculator);
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

    interface ValueGetter {
        String getValue(String name);
    }

    interface ValueCalculator<T> {
        T calculate(String value);
    }
}