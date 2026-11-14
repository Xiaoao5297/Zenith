package com.google.common.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Defaults {

    private static final Map<Class<?>, Object> DEFAULTS;

    private Defaults() {}

    private static <T> void put(Map<Class<?>, Object> map, Class<T> oclass, T t0) {
        map.put(oclass, t0);
    }

    public static <T> T defaultValue(Class<T> oclass) {
        Object object = Defaults.DEFAULTS.get(Preconditions.checkNotNull(oclass));

        return object;
    }

    static {
        HashMap hashmap = new HashMap();

        put(hashmap, Boolean.TYPE, false);
        put(hashmap, Character.TYPE, '\u0000');
        put(hashmap, Byte.TYPE, (byte) 0);
        put(hashmap, Short.TYPE, Short.valueOf((short) 0));
        put(hashmap, Integer.TYPE, 0);
        put(hashmap, Long.TYPE, 0L);
        put(hashmap, Float.TYPE, 0.0F);
        put(hashmap, Double.TYPE, 0.0D);
        DEFAULTS = Collections.unmodifiableMap(hashmap);
    }
}
