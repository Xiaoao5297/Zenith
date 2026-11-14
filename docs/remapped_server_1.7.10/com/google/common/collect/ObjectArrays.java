package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class ObjectArrays {

    static final Object[] EMPTY_ARRAY = new Object[0];

    private ObjectArrays() {}

    @GwtIncompatible("Array.newInstance(Class, int)")
    public static <T> T[] newArray(Class<T> oclass, int i) {
        return (Object[]) ((Object[]) Array.newInstance(oclass, i));
    }

    public static <T> T[] newArray(T[] at, int i) {
        return Platform.newArray(at, i);
    }

    @GwtIncompatible("Array.newInstance(Class, int)")
    public static <T> T[] concat(T[] at, T[] at1, Class<T> oclass) {
        Object[] aobject = newArray(oclass, at.length + at1.length);

        System.arraycopy(at, 0, aobject, 0, at.length);
        System.arraycopy(at1, 0, aobject, at.length, at1.length);
        return aobject;
    }

    public static <T> T[] concat(@Nullable T t0, T[] at) {
        Object[] aobject = newArray(at, at.length + 1);

        aobject[0] = t0;
        System.arraycopy(at, 0, aobject, 1, at.length);
        return aobject;
    }

    public static <T> T[] concat(T[] at, @Nullable T t0) {
        Object[] aobject = arraysCopyOf(at, at.length + 1);

        aobject[at.length] = t0;
        return aobject;
    }

    static <T> T[] arraysCopyOf(T[] at, int i) {
        Object[] aobject = newArray(at, i);

        System.arraycopy(at, 0, aobject, 0, Math.min(at.length, i));
        return aobject;
    }

    static <T> T[] toArrayImpl(Collection<?> collection, T[] at) {
        int i = collection.size();

        if (at.length < i) {
            at = newArray(at, i);
        }

        fillArray(collection, at);
        if (at.length > i) {
            at[i] = null;
        }

        return at;
    }

    static <T> T[] toArrayImpl(Object[] aobject, int i, int j, T[] at) {
        Preconditions.checkPositionIndexes(i, i + j, aobject.length);
        if (at.length < j) {
            at = newArray(at, j);
        } else if (at.length > j) {
            at[j] = null;
        }

        System.arraycopy(aobject, i, at, 0, j);
        return at;
    }

    static Object[] toArrayImpl(Collection<?> collection) {
        return fillArray(collection, new Object[collection.size()]);
    }

    static Object[] copyAsObjectArray(Object[] aobject, int i, int j) {
        Preconditions.checkPositionIndexes(i, i + j, aobject.length);
        if (j == 0) {
            return ObjectArrays.EMPTY_ARRAY;
        } else {
            Object[] aobject1 = new Object[j];

            System.arraycopy(aobject, i, aobject1, 0, j);
            return aobject1;
        }
    }

    private static Object[] fillArray(Iterable<?> iterable, Object[] aobject) {
        int i = 0;

        Object object;

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); aobject[i++] = object) {
            object = iterator.next();
        }

        return aobject;
    }

    static void swap(Object[] aobject, int i, int j) {
        Object object = aobject[i];

        aobject[i] = aobject[j];
        aobject[j] = object;
    }

    static Object[] checkElementsNotNull(Object... aobject) {
        return checkElementsNotNull(aobject, aobject.length);
    }

    static Object[] checkElementsNotNull(Object[] aobject, int i) {
        for (int j = 0; j < i; ++j) {
            checkElementNotNull(aobject[j], j);
        }

        return aobject;
    }

    static Object checkElementNotNull(Object object, int i) {
        if (object == null) {
            throw new NullPointerException("at index " + i);
        } else {
            return object;
        }
    }
}
