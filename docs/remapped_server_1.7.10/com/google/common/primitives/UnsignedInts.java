package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;

@Beta
@GwtCompatible
public final class UnsignedInts {

    static final long INT_MASK = 4294967295L;

    private UnsignedInts() {}

    static int flip(int i) {
        return i ^ Integer.MIN_VALUE;
    }

    public static int compare(int i, int j) {
        return Ints.compare(flip(i), flip(j));
    }

    public static long toLong(int i) {
        return (long) i & 4294967295L;
    }

    public static int min(int... aint) {
        Preconditions.checkArgument(aint.length > 0);
        int i = flip(aint[0]);

        for (int j = 1; j < aint.length; ++j) {
            int k = flip(aint[j]);

            if (k < i) {
                i = k;
            }
        }

        return flip(i);
    }

    public static int max(int... aint) {
        Preconditions.checkArgument(aint.length > 0);
        int i = flip(aint[0]);

        for (int j = 1; j < aint.length; ++j) {
            int k = flip(aint[j]);

            if (k > i) {
                i = k;
            }
        }

        return flip(i);
    }

    public static String join(String s, int... aint) {
        Preconditions.checkNotNull(s);
        if (aint.length == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(aint.length * 5);

            stringbuilder.append(toString(aint[0]));

            for (int i = 1; i < aint.length; ++i) {
                stringbuilder.append(s).append(toString(aint[i]));
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<int[]> lexicographicalComparator() {
        return UnsignedInts.LexicographicalComparator.INSTANCE;
    }

    public static int divide(int i, int j) {
        return (int) (toLong(i) / toLong(j));
    }

    public static int remainder(int i, int j) {
        return (int) (toLong(i) % toLong(j));
    }

    public static int decode(String s) {
        ParseRequest parserequest = ParseRequest.fromString(s);

        try {
            return parseUnsignedInt(parserequest.rawValue, parserequest.radix);
        } catch (NumberFormatException numberformatexception) {
            NumberFormatException numberformatexception1 = new NumberFormatException("Error parsing value: " + s);

            numberformatexception1.initCause(numberformatexception);
            throw numberformatexception1;
        }
    }

    public static int parseUnsignedInt(String s) {
        return parseUnsignedInt(s, 10);
    }

    public static int parseUnsignedInt(String s, int i) {
        Preconditions.checkNotNull(s);
        long j = Long.parseLong(s, i);

        if ((j & 4294967295L) != j) {
            throw new NumberFormatException("Input " + s + " in base " + i + " is not in the range of an unsigned integer");
        } else {
            return (int) j;
        }
    }

    public static String toString(int i) {
        return toString(i, 10);
    }

    public static String toString(int i, int j) {
        long k = (long) i & 4294967295L;

        return Long.toString(k, j);
    }

    static enum LexicographicalComparator implements Comparator<int[]> {

        INSTANCE;

        private LexicographicalComparator() {}

        public int compare(int[] aint, int[] aint1) {
            int i = Math.min(aint.length, aint1.length);

            for (int j = 0; j < i; ++j) {
                if (aint[j] != aint1[j]) {
                    return UnsignedInts.compare(aint[j], aint1[j]);
                }
            }

            return aint.length - aint1.length;
        }
    }
}
