package org.apache.commons.lang3;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class Validate {

    private static final String DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE = "The value %s is not in the specified exclusive range of %s to %s";
    private static final String DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE = "The value %s is not in the specified inclusive range of %s to %s";
    private static final String DEFAULT_MATCHES_PATTERN_EX = "The string %s does not match the pattern %s";
    private static final String DEFAULT_IS_NULL_EX_MESSAGE = "The validated object is null";
    private static final String DEFAULT_IS_TRUE_EX_MESSAGE = "The validated expression is false";
    private static final String DEFAULT_NO_NULL_ELEMENTS_ARRAY_EX_MESSAGE = "The validated array contains null element at index: %d";
    private static final String DEFAULT_NO_NULL_ELEMENTS_COLLECTION_EX_MESSAGE = "The validated collection contains null element at index: %d";
    private static final String DEFAULT_NOT_BLANK_EX_MESSAGE = "The validated character sequence is blank";
    private static final String DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE = "The validated array is empty";
    private static final String DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE = "The validated character sequence is empty";
    private static final String DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE = "The validated collection is empty";
    private static final String DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE = "The validated map is empty";
    private static final String DEFAULT_VALID_INDEX_ARRAY_EX_MESSAGE = "The validated array index is invalid: %d";
    private static final String DEFAULT_VALID_INDEX_CHAR_SEQUENCE_EX_MESSAGE = "The validated character sequence index is invalid: %d";
    private static final String DEFAULT_VALID_INDEX_COLLECTION_EX_MESSAGE = "The validated collection index is invalid: %d";
    private static final String DEFAULT_VALID_STATE_EX_MESSAGE = "The validated state is false";
    private static final String DEFAULT_IS_ASSIGNABLE_EX_MESSAGE = "Cannot assign a %s to a %s";
    private static final String DEFAULT_IS_INSTANCE_OF_EX_MESSAGE = "Expected type: %s, actual: %s";

    public Validate() {}

    public static void isTrue(boolean flag, String s, long i) {
        if (!flag) {
            throw new IllegalArgumentException(String.format(s, i));
        }
    }

    public static void isTrue(boolean flag, String s, double d0) {
        if (!flag) {
            throw new IllegalArgumentException(String.format(s, d0));
        }
    }

    public static void isTrue(boolean flag, String s, Object... aobject) {
        if (!flag) {
            throw new IllegalArgumentException(String.format(s, aobject));
        }
    }

    public static void isTrue(boolean flag) {
        if (!flag) {
            throw new IllegalArgumentException("The validated expression is false");
        }
    }

    public static <T> T notNull(T t0) {
        return notNull(t0, "The validated object is null");
    }

    public static <T> T notNull(T t0, String s, Object... aobject) {
        if (t0 == null) {
            throw new NullPointerException(String.format(s, aobject));
        } else {
            return t0;
        }
    }

    public static <T> T[] notEmpty(T[] at, String s, Object... aobject) {
        if (at == null) {
            throw new NullPointerException(String.format(s, aobject));
        } else if (at.length == 0) {
            throw new IllegalArgumentException(String.format(s, aobject));
        } else {
            return at;
        }
    }

    public static <T> T[] notEmpty(T[] at) {
        return notEmpty(at, "The validated array is empty");
    }

    public static <T extends Collection<?>> T notEmpty(T t0, String s, Object... aobject) {
        if (t0 == null) {
            throw new NullPointerException(String.format(s, aobject));
        } else if (t0.isEmpty()) {
            throw new IllegalArgumentException(String.format(s, aobject));
        } else {
            return t0;
        }
    }

    public static <T extends Collection<?>> T notEmpty(T t0) {
        return notEmpty(t0, "The validated collection is empty");
    }

    public static <T extends Map<?, ?>> T notEmpty(T t0, String s, Object... aobject) {
        if (t0 == null) {
            throw new NullPointerException(String.format(s, aobject));
        } else if (t0.isEmpty()) {
            throw new IllegalArgumentException(String.format(s, aobject));
        } else {
            return t0;
        }
    }

    public static <T extends Map<?, ?>> T notEmpty(T t0) {
        return notEmpty(t0, "The validated map is empty");
    }

    public static <T extends CharSequence> T notEmpty(T t0, String s, Object... aobject) {
        if (t0 == null) {
            throw new NullPointerException(String.format(s, aobject));
        } else if (t0.length() == 0) {
            throw new IllegalArgumentException(String.format(s, aobject));
        } else {
            return t0;
        }
    }

    public static <T extends CharSequence> T notEmpty(T t0) {
        return notEmpty(t0, "The validated character sequence is empty");
    }

    public static <T extends CharSequence> T notBlank(T t0, String s, Object... aobject) {
        if (t0 == null) {
            throw new NullPointerException(String.format(s, aobject));
        } else if (StringUtils.isBlank(t0)) {
            throw new IllegalArgumentException(String.format(s, aobject));
        } else {
            return t0;
        }
    }

    public static <T extends CharSequence> T notBlank(T t0) {
        return notBlank(t0, "The validated character sequence is blank");
    }

    public static <T> T[] noNullElements(T[] at, String s, Object... aobject) {
        notNull(at);

        for (int i = 0; i < at.length; ++i) {
            if (at[i] == null) {
                Object[] aobject1 = ArrayUtils.add(aobject, i);

                throw new IllegalArgumentException(String.format(s, aobject1));
            }
        }

        return at;
    }

    public static <T> T[] noNullElements(T[] at) {
        return noNullElements(at, "The validated array contains null element at index: %d");
    }

    public static <T extends Iterable<?>> T noNullElements(T t0, String s, Object... aobject) {
        notNull(t0);
        int i = 0;

        for (Iterator iterator = t0.iterator(); iterator.hasNext(); ++i) {
            if (iterator.next() == null) {
                Object[] aobject1 = ArrayUtils.addAll(aobject, i);

                throw new IllegalArgumentException(String.format(s, aobject1));
            }
        }

        return t0;
    }

    public static <T extends Iterable<?>> T noNullElements(T t0) {
        return noNullElements(t0, "The validated collection contains null element at index: %d");
    }

    public static <T> T[] validIndex(T[] at, int i, String s, Object... aobject) {
        notNull(at);
        if (i >= 0 && i < at.length) {
            return at;
        } else {
            throw new IndexOutOfBoundsException(String.format(s, aobject));
        }
    }

    public static <T> T[] validIndex(T[] at, int i) {
        return validIndex(at, i, "The validated array index is invalid: %d", i);
    }

    public static <T extends Collection<?>> T validIndex(T t0, int i, String s, Object... aobject) {
        notNull(t0);
        if (i >= 0 && i < t0.size()) {
            return t0;
        } else {
            throw new IndexOutOfBoundsException(String.format(s, aobject));
        }
    }

    public static <T extends Collection<?>> T validIndex(T t0, int i) {
        return validIndex(t0, i, "The validated collection index is invalid: %d", i);
    }

    public static <T extends CharSequence> T validIndex(T t0, int i, String s, Object... aobject) {
        notNull(t0);
        if (i >= 0 && i < t0.length()) {
            return t0;
        } else {
            throw new IndexOutOfBoundsException(String.format(s, aobject));
        }
    }

    public static <T extends CharSequence> T validIndex(T t0, int i) {
        return validIndex(t0, i, "The validated character sequence index is invalid: %d", i);
    }

    public static void validState(boolean flag) {
        if (!flag) {
            throw new IllegalStateException("The validated state is false");
        }
    }

    public static void validState(boolean flag, String s, Object... aobject) {
        if (!flag) {
            throw new IllegalStateException(String.format(s, aobject));
        }
    }

    public static void matchesPattern(CharSequence charsequence, String s) {
        if (!Pattern.matches(s, charsequence)) {
            throw new IllegalArgumentException(String.format("The string %s does not match the pattern %s", charsequence, s));
        }
    }

    public static void matchesPattern(CharSequence charsequence, String s, String s1, Object... aobject) {
        if (!Pattern.matches(s, charsequence)) {
            throw new IllegalArgumentException(String.format(s1, aobject));
        }
    }

    public static <T> void inclusiveBetween(T t0, T t1, Comparable<T> comparable) {
        if (comparable.compareTo(t0) < 0 || comparable.compareTo(t1) > 0) {
            throw new IllegalArgumentException(String.format("The value %s is not in the specified inclusive range of %s to %s", comparable, t0, t1));
        }
    }

    public static <T> void inclusiveBetween(T t0, T t1, Comparable<T> comparable, String s, Object... aobject) {
        if (comparable.compareTo(t0) < 0 || comparable.compareTo(t1) > 0) {
            throw new IllegalArgumentException(String.format(s, aobject));
        }
    }

    public static <T> void exclusiveBetween(T t0, T t1, Comparable<T> comparable) {
        if (comparable.compareTo(t0) <= 0 || comparable.compareTo(t1) >= 0) {
            throw new IllegalArgumentException(String.format("The value %s is not in the specified exclusive range of %s to %s", comparable, t0, t1));
        }
    }

    public static <T> void exclusiveBetween(T t0, T t1, Comparable<T> comparable, String s, Object... aobject) {
        if (comparable.compareTo(t0) <= 0 || comparable.compareTo(t1) >= 0) {
            throw new IllegalArgumentException(String.format(s, aobject));
        }
    }

    public static void isInstanceOf(Class<?> oclass, Object object) {
        if (!oclass.isInstance(object)) {
            throw new IllegalArgumentException(String.format("Expected type: %s, actual: %s", oclass.getName(), object == null ? "null" : object.getClass().getName()));
        }
    }

    public static void isInstanceOf(Class<?> oclass, Object object, String s, Object... aobject) {
        if (!oclass.isInstance(object)) {
            throw new IllegalArgumentException(String.format(s, aobject));
        }
    }

    public static void isAssignableFrom(Class<?> oclass, Class<?> oclass1) {
        if (!oclass.isAssignableFrom(oclass1)) {
            throw new IllegalArgumentException(String.format("Cannot assign a %s to a %s", oclass1 == null ? "null" : oclass1.getName(), oclass.getName()));
        }
    }

    public static void isAssignableFrom(Class<?> oclass, Class<?> oclass1, String s, Object... aobject) {
        if (!oclass.isAssignableFrom(oclass1)) {
            throw new IllegalArgumentException(String.format(s, aobject));
        }
    }
}
