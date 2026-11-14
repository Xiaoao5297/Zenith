package org.apache.commons.lang3;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;
import org.apache.commons.lang3.exception.CloneFailedException;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.StrBuilder;

public class ObjectUtils {

    public static final ObjectUtils.Null NULL = new ObjectUtils.Null();

    public ObjectUtils() {}

    public static <T> T defaultIfNull(T t0, T t1) {
        return t0 != null ? t0 : t1;
    }

    public static <T> T firstNonNull(T... at) {
        if (at != null) {
            Object[] aobject = at;
            int i = at.length;

            for (int j = 0; j < i; ++j) {
                Object object = aobject[j];

                if (object != null) {
                    return object;
                }
            }
        }

        return null;
    }

    /** @deprecated */
    @Deprecated
    public static boolean equals(Object object, Object object1) {
        return object == object1 ? true : (object != null && object1 != null ? object.equals(object1) : false);
    }

    public static boolean notEqual(Object object, Object object1) {
        return !equals(object, object1);
    }

    /** @deprecated */
    @Deprecated
    public static int hashCode(Object object) {
        return object == null ? 0 : object.hashCode();
    }

    /** @deprecated */
    @Deprecated
    public static int hashCodeMulti(Object... aobject) {
        int i = 1;

        if (aobject != null) {
            Object[] aobject1 = aobject;
            int j = aobject.length;

            for (int k = 0; k < j; ++k) {
                Object object = aobject1[k];

                i = i * 31 + hashCode(object);
            }
        }

        return i;
    }

    public static String identityToString(Object object) {
        if (object == null) {
            return null;
        } else {
            StringBuilder stringbuilder = new StringBuilder();

            identityToString(stringbuilder, object);
            return stringbuilder.toString();
        }
    }

    public static void identityToString(Appendable appendable, Object object) throws IOException {
        if (object == null) {
            throw new NullPointerException("Cannot get the toString of a null identity");
        } else {
            appendable.append(object.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(object)));
        }
    }

    public static void identityToString(StrBuilder strbuilder, Object object) {
        if (object == null) {
            throw new NullPointerException("Cannot get the toString of a null identity");
        } else {
            strbuilder.append(object.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(object)));
        }
    }

    public static void identityToString(StringBuffer stringbuffer, Object object) {
        if (object == null) {
            throw new NullPointerException("Cannot get the toString of a null identity");
        } else {
            stringbuffer.append(object.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(object)));
        }
    }

    public static void identityToString(StringBuilder stringbuilder, Object object) {
        if (object == null) {
            throw new NullPointerException("Cannot get the toString of a null identity");
        } else {
            stringbuilder.append(object.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(object)));
        }
    }

    /** @deprecated */
    @Deprecated
    public static String toString(Object object) {
        return object == null ? "" : object.toString();
    }

    /** @deprecated */
    @Deprecated
    public static String toString(Object object, String s) {
        return object == null ? s : object.toString();
    }

    public static <T extends Comparable<? super T>> T min(T... at) {
        Comparable comparable = null;

        if (at != null) {
            Comparable[] acomparable = at;
            int i = at.length;

            for (int j = 0; j < i; ++j) {
                Comparable comparable1 = acomparable[j];

                if (compare(comparable1, comparable, true) < 0) {
                    comparable = comparable1;
                }
            }
        }

        return comparable;
    }

    public static <T extends Comparable<? super T>> T max(T... at) {
        Comparable comparable = null;

        if (at != null) {
            Comparable[] acomparable = at;
            int i = at.length;

            for (int j = 0; j < i; ++j) {
                Comparable comparable1 = acomparable[j];

                if (compare(comparable1, comparable, false) > 0) {
                    comparable = comparable1;
                }
            }
        }

        return comparable;
    }

    public static <T extends Comparable<? super T>> int compare(T t0, T t1) {
        return compare(t0, t1, false);
    }

    public static <T extends Comparable<? super T>> int compare(T t0, T t1, boolean flag) {
        return t0 == t1 ? 0 : (t0 == null ? (flag ? 1 : -1) : (t1 == null ? (flag ? -1 : 1) : t0.compareTo(t1)));
    }

    public static <T extends Comparable<? super T>> T median(T... at) {
        Validate.notEmpty((Object[]) at);
        Validate.noNullElements((Object[]) at);
        TreeSet treeset = new TreeSet();

        Collections.addAll(treeset, at);
        Comparable comparable = (Comparable) treeset.toArray()[(treeset.size() - 1) / 2];

        return comparable;
    }

    public static <T> T median(Comparator<T> comparator, T... at) {
        Validate.notEmpty(at, "null/empty items");
        Validate.noNullElements(at);
        Validate.notNull(comparator, "null comparator");
        TreeSet treeset = new TreeSet(comparator);

        Collections.addAll(treeset, at);
        Object object = treeset.toArray()[(treeset.size() - 1) / 2];

        return object;
    }

    public static <T> T mode(T... at) {
        if (ArrayUtils.isNotEmpty(at)) {
            HashMap hashmap = new HashMap(at.length);
            Object[] aobject = at;
            int i = at.length;

            for (int j = 0; j < i; ++j) {
                Object object = aobject[j];
                MutableInt mutableint = (MutableInt) hashmap.get(object);

                if (mutableint == null) {
                    hashmap.put(object, new MutableInt(1));
                } else {
                    mutableint.increment();
                }
            }

            Object object1 = null;

            i = 0;
            Iterator iterator = hashmap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                int k = ((MutableInt) entry.getValue()).intValue();

                if (k == i) {
                    object1 = null;
                } else if (k > i) {
                    i = k;
                    object1 = entry.getKey();
                }
            }

            return object1;
        } else {
            return null;
        }
    }

    public static <T> T clone(T t0) {
        if (!(t0 instanceof Cloneable)) {
            return null;
        } else {
            Object object;

            if (t0.getClass().isArray()) {
                Class oclass = t0.getClass().getComponentType();

                if (!oclass.isPrimitive()) {
                    object = ((Object[]) ((Object[]) t0)).clone();
                } else {
                    int i = Array.getLength(t0);

                    object = Array.newInstance(oclass, i);

                    while (i-- > 0) {
                        Array.set(object, i, Array.get(t0, i));
                    }
                }
            } else {
                try {
                    Method method = t0.getClass().getMethod("clone");

                    object = method.invoke(t0);
                } catch (NoSuchMethodException nosuchmethodexception) {
                    throw new CloneFailedException("Cloneable type " + t0.getClass().getName() + " has no clone method", nosuchmethodexception);
                } catch (IllegalAccessException illegalaccessexception) {
                    throw new CloneFailedException("Cannot clone Cloneable type " + t0.getClass().getName(), illegalaccessexception);
                } catch (InvocationTargetException invocationtargetexception) {
                    throw new CloneFailedException("Exception cloning Cloneable type " + t0.getClass().getName(), invocationtargetexception.getCause());
                }
            }

            return object;
        }
    }

    public static <T> T cloneIfPossible(T t0) {
        Object object = clone(t0);

        return object == null ? t0 : object;
    }

    public static boolean CONST(boolean flag) {
        return flag;
    }

    public static byte CONST(byte b0) {
        return b0;
    }

    public static byte CONST_BYTE(int i) throws IllegalArgumentException {
        if (i >= -128 && i <= 127) {
            return (byte) i;
        } else {
            throw new IllegalArgumentException("Supplied value must be a valid byte literal between -128 and 127: [" + i + "]");
        }
    }

    public static char CONST(char c0) {
        return c0;
    }

    public static short CONST(short short0) {
        return short0;
    }

    public static short CONST_SHORT(int i) throws IllegalArgumentException {
        if (i >= -32768 && i <= 32767) {
            return (short) i;
        } else {
            throw new IllegalArgumentException("Supplied value must be a valid byte literal between -32768 and 32767: [" + i + "]");
        }
    }

    public static int CONST(int i) {
        return i;
    }

    public static long CONST(long i) {
        return i;
    }

    public static float CONST(float f) {
        return f;
    }

    public static double CONST(double d0) {
        return d0;
    }

    public static <T> T CONST(T t0) {
        return t0;
    }

    public static class Null implements Serializable {

        private static final long serialVersionUID = 7092611880189329093L;

        Null() {}

        private Object readResolve() {
            return ObjectUtils.NULL;
        }
    }
}
