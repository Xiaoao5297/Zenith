package com.google.gson.internal;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Primitives {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;

    private Primitives() {}

    private static void add(Map<Class<?>, Class<?>> map, Map<Class<?>, Class<?>> map1, Class<?> oclass, Class<?> oclass1) {
        map.put(oclass, oclass1);
        map1.put(oclass1, oclass);
    }

    public static boolean isPrimitive(Type type) {
        return Primitives.PRIMITIVE_TO_WRAPPER_TYPE.containsKey(type);
    }

    public static boolean isWrapperType(Type type) {
        return Primitives.WRAPPER_TO_PRIMITIVE_TYPE.containsKey($Gson$Preconditions.checkNotNull(type));
    }

    public static <T> Class<T> wrap(Class<T> oclass) {
        Class oclass1 = (Class) Primitives.PRIMITIVE_TO_WRAPPER_TYPE.get($Gson$Preconditions.checkNotNull(oclass));

        return oclass1 == null ? oclass : oclass1;
    }

    public static <T> Class<T> unwrap(Class<T> oclass) {
        Class oclass1 = (Class) Primitives.WRAPPER_TO_PRIMITIVE_TYPE.get($Gson$Preconditions.checkNotNull(oclass));

        return oclass1 == null ? oclass : oclass1;
    }

    static {
        HashMap hashmap = new HashMap(16);
        HashMap hashmap1 = new HashMap(16);

        add(hashmap, hashmap1, Boolean.TYPE, Boolean.class);
        add(hashmap, hashmap1, Byte.TYPE, Byte.class);
        add(hashmap, hashmap1, Character.TYPE, Character.class);
        add(hashmap, hashmap1, Double.TYPE, Double.class);
        add(hashmap, hashmap1, Float.TYPE, Float.class);
        add(hashmap, hashmap1, Integer.TYPE, Integer.class);
        add(hashmap, hashmap1, Long.TYPE, Long.class);
        add(hashmap, hashmap1, Short.TYPE, Short.class);
        add(hashmap, hashmap1, Void.TYPE, Void.class);
        PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(hashmap);
        WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(hashmap1);
    }
}
