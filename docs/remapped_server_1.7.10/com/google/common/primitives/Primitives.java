package com.google.common.primitives;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Primitives {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;

    private Primitives() {}

    private static void add(Map<Class<?>, Class<?>> map, Map<Class<?>, Class<?>> map1, Class<?> oclass, Class<?> oclass1) {
        map.put(oclass, oclass1);
        map1.put(oclass1, oclass);
    }

    public static Set<Class<?>> allPrimitiveTypes() {
        return Primitives.PRIMITIVE_TO_WRAPPER_TYPE.keySet();
    }

    public static Set<Class<?>> allWrapperTypes() {
        return Primitives.WRAPPER_TO_PRIMITIVE_TYPE.keySet();
    }

    public static boolean isWrapperType(Class<?> oclass) {
        return Primitives.WRAPPER_TO_PRIMITIVE_TYPE.containsKey(Preconditions.checkNotNull(oclass));
    }

    public static <T> Class<T> wrap(Class<T> oclass) {
        Preconditions.checkNotNull(oclass);
        Class oclass1 = (Class) Primitives.PRIMITIVE_TO_WRAPPER_TYPE.get(oclass);

        return oclass1 == null ? oclass : oclass1;
    }

    public static <T> Class<T> unwrap(Class<T> oclass) {
        Preconditions.checkNotNull(oclass);
        Class oclass1 = (Class) Primitives.WRAPPER_TO_PRIMITIVE_TYPE.get(oclass);

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
