package com.fineio.v3.cache.base;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * This class created on 2019/4/11
 *
 * @author Lucifer
 * @description
 */
public final class UnsafeAccess {
    static final String ANDROID = "THE_ONE";
    static final String OPEN_JDK = "theUnsafe";
    public static final Unsafe UNSAFE;

    private UnsafeAccess() {
    }

    static {
        try {
            UNSAFE = load(OPEN_JDK, ANDROID);
        } catch (Exception e) {
            throw new Error("Failed to load sun.misc.Unsafe", e);
        }
    }

    /**
     * 获得字段地址的offset
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static long objectFieldOffset(Class<?> clazz, String fieldName) {
        try {
            return UNSAFE.objectFieldOffset(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException | SecurityException e) {
            throw new Error(e);
        }
    }

    static Unsafe load(String openJdk, String android) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        Field field;
        try {
            field = Unsafe.class.getDeclaredField(openJdk);
        } catch (NoSuchFieldException e) {
            try {
                field = Unsafe.class.getDeclaredField(android);
            } catch (NoSuchFieldException e2) {
                Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
                unsafeConstructor.setAccessible(true);
                return unsafeConstructor.newInstance();
            }
        }
        field.setAccessible(true);
        return (Unsafe) field.get(null);
    }
}
