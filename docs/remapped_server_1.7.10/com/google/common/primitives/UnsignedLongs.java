package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.math.BigInteger;
import java.util.Comparator;

@Beta
@GwtCompatible
public final class UnsignedLongs {

    public static final long MAX_VALUE = -1L;
    private static final long[] maxValueDivs = new long[37];
    private static final int[] maxValueMods = new int[37];
    private static final int[] maxSafeDigits = new int[37];

    private UnsignedLongs() {}

    private static long flip(long i) {
        return i ^ Long.MIN_VALUE;
    }

    public static int compare(long i, long j) {
        return Longs.compare(flip(i), flip(j));
    }

    public static long min(long... along) {
        Preconditions.checkArgument(along.length > 0);
        long i = flip(along[0]);

        for (int j = 1; j < along.length; ++j) {
            long k = flip(along[j]);

            if (k < i) {
                i = k;
            }
        }

        return flip(i);
    }

    public static long max(long... along) {
        Preconditions.checkArgument(along.length > 0);
        long i = flip(along[0]);

        for (int j = 1; j < along.length; ++j) {
            long k = flip(along[j]);

            if (k > i) {
                i = k;
            }
        }

        return flip(i);
    }

    public static String join(String s, long... along) {
        Preconditions.checkNotNull(s);
        if (along.length == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(along.length * 5);

            stringbuilder.append(toString(along[0]));

            for (int i = 1; i < along.length; ++i) {
                stringbuilder.append(s).append(toString(along[i]));
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<long[]> lexicographicalComparator() {
        return UnsignedLongs.LexicographicalComparator.INSTANCE;
    }

    public static long divide(long i, long j) {
        if (j < 0L) {
            return compare(i, j) < 0 ? 0L : 1L;
        } else if (i >= 0L) {
            return i / j;
        } else {
            long k = (i >>> 1) / j << 1;
            long l = i - k * j;

            return k + (long) (compare(l, j) >= 0 ? 1 : 0);
        }
    }

    public static long remainder(long i, long j) {
        if (j < 0L) {
            return compare(i, j) < 0 ? i : i - j;
        } else if (i >= 0L) {
            return i % j;
        } else {
            long k = (i >>> 1) / j << 1;
            long l = i - k * j;

            return l - (compare(l, j) >= 0 ? j : 0L);
        }
    }

    public static long parseUnsignedLong(String s) {
        return parseUnsignedLong(s, 10);
    }

    public static long decode(String s) {
        ParseRequest parserequest = ParseRequest.fromString(s);

        try {
            return parseUnsignedLong(parserequest.rawValue, parserequest.radix);
        } catch (NumberFormatException numberformatexception) {
            NumberFormatException numberformatexception1 = new NumberFormatException("Error parsing value: " + s);

            numberformatexception1.initCause(numberformatexception);
            throw numberformatexception1;
        }
    }

    public static long parseUnsignedLong(String s, int i) {
        Preconditions.checkNotNull(s);
        if (s.length() == 0) {
            throw new NumberFormatException("empty string");
        } else if (i >= 2 && i <= 36) {
            int j = UnsignedLongs.maxSafeDigits[i] - 1;
            long k = 0L;

            for (int l = 0; l < s.length(); ++l) {
                int i1 = Character.digit(s.charAt(l), i);

                if (i1 == -1) {
                    throw new NumberFormatException(s);
                }

                if (l > j && overflowInParse(k, i1, i)) {
                    throw new NumberFormatException("Too large for unsigned long: " + s);
                }

                k = k * (long) i + (long) i1;
            }

            return k;
        } else {
            throw new NumberFormatException("illegal radix: " + i);
        }
    }

    private static boolean overflowInParse(long i, int j, int k) {
        return i >= 0L ? (i < UnsignedLongs.maxValueDivs[k] ? false : (i > UnsignedLongs.maxValueDivs[k] ? true : j > UnsignedLongs.maxValueMods[k])) : true;
    }

    public static String toString(long i) {
        return toString(i, 10);
    }

    public static String toString(long i, int j) {
        Preconditions.checkArgument(j >= 2 && j <= 36, "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", j);
        if (i == 0L) {
            return "0";
        } else {
            char[] achar = new char[64];
            int k = achar.length;

            if (i < 0L) {
                long l = divide(i, (long) j);
                long i1 = i - l * (long) j;

                --k;
                achar[k] = Character.forDigit((int) i1, j);
                i = l;
            }

            while (i > 0L) {
                --k;
                achar[k] = Character.forDigit((int) (i % (long) j), j);
                i /= (long) j;
            }

            return new String(achar, k, achar.length - k);
        }
    }

    static {
        BigInteger biginteger = new BigInteger("10000000000000000", 16);

        for (int i = 2; i <= 36; ++i) {
            UnsignedLongs.maxValueDivs[i] = divide(-1L, (long) i);
            UnsignedLongs.maxValueMods[i] = (int) remainder(-1L, (long) i);
            UnsignedLongs.maxSafeDigits[i] = biginteger.toString(i).length() - 1;
        }

    }

    static enum LexicographicalComparator implements Comparator<long[]> {

        INSTANCE;

        private LexicographicalComparator() {}

        public int compare(long[] along, long[] along1) {
            int i = Math.min(along.length, along1.length);

            for (int j = 0; j < i; ++j) {
                if (along[j] != along1[j]) {
                    return UnsignedLongs.compare(along[j], along1[j]);
                }
            }

            return along.length - along1.length;
        }
    }
}
