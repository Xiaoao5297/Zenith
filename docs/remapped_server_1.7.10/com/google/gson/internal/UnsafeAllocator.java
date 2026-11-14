package com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class UnsafeAllocator {

    public UnsafeAllocator() {}

    public abstract <T> T newInstance(Class<T> oclass) throws Exception;

    public static UnsafeAllocator create() {
        try {
            Class oclass = Class.forName("sun.misc.Unsafe");
            Field field = oclass.getDeclaredField("theUnsafe");

            field.setAccessible(true);
            final Object object = field.get((Object) null);
            final Method method = oclass.getMethod("allocateInstance", Class.class);

            return new UnsafeAllocator() {
                @Override
                public <T> T newInstance(Class<T> oclass1) throws Exception {
                    return method.invoke(object, oclass1);
                }
            };
        } catch (Exception exception) {
            final Method method1;

            try {
                method1 = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
                method1.setAccessible(true);
                return new UnsafeAllocator() {
                    @Override
                    public <T> T newInstance(Class<T> oclass1) throws Exception {
                        return method1.invoke((Object) null, oclass1, Object.class);
                    }
                };
            } catch (Exception exception1) {
                try {
                    method1 = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
                    method1.setAccessible(true);
                    final int i = (Integer) method1.invoke((Object) null, Object.class);
                    final Method method2 = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Integer.TYPE);

                    method2.setAccessible(true);
                    return new UnsafeAllocator() {
                        @Override
                        public <T> T newInstance(Class<T> oclass1) throws Exception {
                            return method2.invoke((Object) null, oclass1, i);
                        }
                    };
                } catch (Exception exception2) {
                    return new UnsafeAllocator() {
                        @Override
                        public <T> T newInstance(Class<T> oclass1) {
                            throw new UnsupportedOperationException("Cannot allocate " + oclass1);
                        }
                    };
                }
            }
        }
    }
}
