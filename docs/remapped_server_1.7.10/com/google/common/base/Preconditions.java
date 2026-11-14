package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public final class Preconditions {

    private Preconditions() {}

    public static void checkArgument(boolean flag) {
        if (!flag) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean flag, @Nullable Object object) {
        if (!flag) {
            throw new IllegalArgumentException(String.valueOf(object));
        }
    }

    public static void checkArgument(boolean flag, @Nullable String s, @Nullable Object... aobject) {
        if (!flag) {
            throw new IllegalArgumentException(format(s, aobject));
        }
    }

    public static void checkState(boolean flag) {
        if (!flag) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean flag, @Nullable Object object) {
        if (!flag) {
            throw new IllegalStateException(String.valueOf(object));
        }
    }

    public static void checkState(boolean flag, @Nullable String s, @Nullable Object... aobject) {
        if (!flag) {
            throw new IllegalStateException(format(s, aobject));
        }
    }

    public static <T> T checkNotNull(T t0) {
        if (t0 == null) {
            throw new NullPointerException();
        } else {
            return t0;
        }
    }

    public static <T> T checkNotNull(T t0, @Nullable Object object) {
        if (t0 == null) {
            throw new NullPointerException(String.valueOf(object));
        } else {
            return t0;
        }
    }

    public static <T> T checkNotNull(T t0, @Nullable String s, @Nullable Object... aobject) {
        if (t0 == null) {
            throw new NullPointerException(format(s, aobject));
        } else {
            return t0;
        }
    }

    public static int checkElementIndex(int i, int j) {
        return checkElementIndex(i, j, "index");
    }

    public static int checkElementIndex(int i, int j, @Nullable String s) {
        if (i >= 0 && i < j) {
            return i;
        } else {
            throw new IndexOutOfBoundsException(badElementIndex(i, j, s));
        }
    }

    private static String badElementIndex(int i, int j, String s) {
        if (i < 0) {
            return format("%s (%s) must not be negative", s, i);
        } else if (j < 0) {
            throw new IllegalArgumentException("negative size: " + j);
        } else {
            return format("%s (%s) must be less than size (%s)", s, i, j);
        }
    }

    public static int checkPositionIndex(int i, int j) {
        return checkPositionIndex(i, j, "index");
    }

    public static int checkPositionIndex(int i, int j, @Nullable String s) {
        if (i >= 0 && i <= j) {
            return i;
        } else {
            throw new IndexOutOfBoundsException(badPositionIndex(i, j, s));
        }
    }

    private static String badPositionIndex(int i, int j, String s) {
        if (i < 0) {
            return format("%s (%s) must not be negative", s, i);
        } else if (j < 0) {
            throw new IllegalArgumentException("negative size: " + j);
        } else {
            return format("%s (%s) must not be greater than size (%s)", s, i, j);
        }
    }

    public static void checkPositionIndexes(int i, int j, int k) {
        if (i < 0 || j < i || j > k) {
            throw new IndexOutOfBoundsException(badPositionIndexes(i, j, k));
        }
    }

    private static String badPositionIndexes(int i, int j, int k) {
        return i >= 0 && i <= k ? (j >= 0 && j <= k ? format("end index (%s) must not be less than start index (%s)", j, i) : badPositionIndex(j, k, "end index")) : badPositionIndex(i, k, "start index");
    }

    static String format(String s, @Nullable Object... aobject) {
        s = String.valueOf(s);
        StringBuilder stringbuilder = new StringBuilder(s.length() + 16 * aobject.length);
        int i = 0;

        int j;
        int k;

        for (k = 0; k < aobject.length; i = j + 2) {
            j = s.indexOf("%s", i);
            if (j == -1) {
                break;
            }

            stringbuilder.append(s.substring(i, j));
            stringbuilder.append(aobject[k++]);
        }

        stringbuilder.append(s.substring(i));
        if (k < aobject.length) {
            stringbuilder.append(" [");
            stringbuilder.append(aobject[k++]);

            while (k < aobject.length) {
                stringbuilder.append(", ");
                stringbuilder.append(aobject[k++]);
            }

            stringbuilder.append(']');
        }

        return stringbuilder.toString();
    }
}
