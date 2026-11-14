package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Comparator;
import sun.misc.Unsafe;

public final class UnsignedBytes {

    public static final byte MAX_POWER_OF_TWO = Byte.MIN_VALUE;
    public static final byte MAX_VALUE = -1;
    private static final int UNSIGNED_MASK = 255;

    private UnsignedBytes() {}

    public static int toInt(byte b0) {
        return b0 & 255;
    }

    public static byte checkedCast(long i) {
        if (i >> 8 != 0L) {
            throw new IllegalArgumentException("Out of range: " + i);
        } else {
            return (byte) ((int) i);
        }
    }

    public static byte saturatedCast(long i) {
        return i > (long) toInt((byte) -1) ? -1 : (i < 0L ? 0 : (byte) ((int) i));
    }

    public static int compare(byte b0, byte b1) {
        return toInt(b0) - toInt(b1);
    }

    public static byte min(byte... abyte) {
        Preconditions.checkArgument(abyte.length > 0);
        int i = toInt(abyte[0]);

        for (int j = 1; j < abyte.length; ++j) {
            int k = toInt(abyte[j]);

            if (k < i) {
                i = k;
            }
        }

        return (byte) i;
    }

    public static byte max(byte... abyte) {
        Preconditions.checkArgument(abyte.length > 0);
        int i = toInt(abyte[0]);

        for (int j = 1; j < abyte.length; ++j) {
            int k = toInt(abyte[j]);

            if (k > i) {
                i = k;
            }
        }

        return (byte) i;
    }

    @Beta
    public static String toString(byte b0) {
        return toString(b0, 10);
    }

    @Beta
    public static String toString(byte b0, int i) {
        Preconditions.checkArgument(i >= 2 && i <= 36, "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", i);
        return Integer.toString(toInt(b0), i);
    }

    @Beta
    public static byte parseUnsignedByte(String s) {
        return parseUnsignedByte(s, 10);
    }

    @Beta
    public static byte parseUnsignedByte(String s, int i) {
        int j = Integer.parseInt((String) Preconditions.checkNotNull(s), i);

        if (j >> 8 == 0) {
            return (byte) j;
        } else {
            throw new NumberFormatException("out of range: " + j);
        }
    }

    public static String join(String s, byte... abyte) {
        Preconditions.checkNotNull(s);
        if (abyte.length == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(abyte.length * (3 + s.length()));

            stringbuilder.append(toInt(abyte[0]));

            for (int i = 1; i < abyte.length; ++i) {
                stringbuilder.append(s).append(toString(abyte[i]));
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<byte[]> lexicographicalComparator() {
        return UnsignedBytes.LexicographicalComparatorHolder.BEST_COMPARATOR;
    }

    @VisibleForTesting
    static Comparator<byte[]> lexicographicalComparatorJavaImpl() {
        return UnsignedBytes.LexicographicalComparatorHolder.PureJavaComparator.INSTANCE;
    }

    @VisibleForTesting
    static class LexicographicalComparatorHolder {

        static final String UNSAFE_COMPARATOR_NAME = UnsignedBytes.LexicographicalComparatorHolder.class.getName() + "$UnsafeComparator";
        static final Comparator<byte[]> BEST_COMPARATOR = getBestComparator();

        LexicographicalComparatorHolder() {}

        static Comparator<byte[]> getBestComparator() {
            try {
                Class oclass = Class.forName(UnsignedBytes.LexicographicalComparatorHolder.UNSAFE_COMPARATOR_NAME);
                Comparator comparator = (Comparator) oclass.getEnumConstants()[0];

                return comparator;
            } catch (Throwable throwable) {
                return UnsignedBytes.lexicographicalComparatorJavaImpl();
            }
        }

        static enum PureJavaComparator implements Comparator<byte[]> {

            INSTANCE;

            private PureJavaComparator() {}

            public int compare(byte[] abyte, byte[] abyte1) {
                int i = Math.min(abyte.length, abyte1.length);

                for (int j = 0; j < i; ++j) {
                    int k = UnsignedBytes.compare(abyte[j], abyte1[j]);

                    if (k != 0) {
                        return k;
                    }
                }

                return abyte.length - abyte1.length;
            }
        }

        @VisibleForTesting
        static enum UnsafeComparator implements Comparator<byte[]> {

            INSTANCE;

            static final boolean BIG_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
            static final Unsafe theUnsafe = getUnsafe();
            static final int BYTE_ARRAY_BASE_OFFSET = UnsignedBytes.LexicographicalComparatorHolder.UnsafeComparator.theUnsafe.arrayBaseOffset(byte[].class);

            private UnsafeComparator() {}

            private static Unsafe getUnsafe() {
                try {
                    return Unsafe.getUnsafe();
                } catch (SecurityException securityexception) {
                    try {
                        return (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                            public Unsafe run() throws Exception {
                                Class oclass = Unsafe.class;
                                Field[] afield = oclass.getDeclaredFields();
                                int i = afield.length;

                                for (int j = 0; j < i; ++j) {
                                    Field field = afield[j];

                                    field.setAccessible(true);
                                    Object object = field.get((Object) null);

                                    if (oclass.isInstance(object)) {
                                        return (Unsafe) oclass.cast(object);
                                    }
                                }

                                throw new NoSuchFieldError("the Unsafe");
                            }
                        });
                    } catch (PrivilegedActionException privilegedactionexception) {
                        throw new RuntimeException("Could not initialize intrinsics", privilegedactionexception.getCause());
                    }
                }
            }

            public int compare(byte[] abyte, byte[] abyte1) {
                int i = Math.min(abyte.length, abyte1.length);
                int j = i / 8;

                int k;

                for (k = 0; k < j * 8; k += 8) {
                    long l = UnsignedBytes.LexicographicalComparatorHolder.UnsafeComparator.theUnsafe.getLong(abyte, (long) UnsignedBytes.LexicographicalComparatorHolder.UnsafeComparator.BYTE_ARRAY_BASE_OFFSET + (long) k);
                    long i1 = UnsignedBytes.LexicographicalComparatorHolder.UnsafeComparator.theUnsafe.getLong(abyte1, (long) UnsignedBytes.LexicographicalComparatorHolder.UnsafeComparator.BYTE_ARRAY_BASE_OFFSET + (long) k);

                    if (l != i1) {
                        if (UnsignedBytes.LexicographicalComparatorHolder.UnsafeComparator.BIG_ENDIAN) {
                            return UnsignedLongs.compare(l, i1);
                        }

                        int j1 = Long.numberOfTrailingZeros(l ^ i1) & -8;

                        return (int) ((l >>> j1 & 255L) - (i1 >>> j1 & 255L));
                    }
                }

                for (k = j * 8; k < i; ++k) {
                    int k1 = UnsignedBytes.compare(abyte[k], abyte1[k]);

                    if (k1 != 0) {
                        return k1;
                    }
                }

                return abyte.length - abyte1.length;
            }

            static {
                if (UnsignedBytes.LexicographicalComparatorHolder.UnsafeComparator.theUnsafe.arrayIndexScale(byte[].class) != 1) {
                    throw new AssertionError();
                }
            }
        }
    }
}
