package com.google.common.math;

import com.google.common.base.Preconditions;
import java.math.BigInteger;

final class DoubleUtils {

    static final long SIGNIFICAND_MASK = 4503599627370495L;
    static final long EXPONENT_MASK = 9218868437227405312L;
    static final long SIGN_MASK = Long.MIN_VALUE;
    static final int SIGNIFICAND_BITS = 52;
    static final int EXPONENT_BIAS = 1023;
    static final long IMPLICIT_BIT = 4503599627370496L;
    private static final long ONE_BITS = Double.doubleToRawLongBits(1.0D);

    private DoubleUtils() {}

    static double nextDown(double d0) {
        return -Math.nextUp(-d0);
    }

    static long getSignificand(double d0) {
        Preconditions.checkArgument(isFinite(d0), "not a normal value");
        int i = Math.getExponent(d0);
        long j = Double.doubleToRawLongBits(d0);

        j &= 4503599627370495L;
        return i == -1023 ? j << 1 : j | 4503599627370496L;
    }

    static boolean isFinite(double d0) {
        return Math.getExponent(d0) <= 1023;
    }

    static boolean isNormal(double d0) {
        return Math.getExponent(d0) >= -1022;
    }

    static double scaleNormalize(double d0) {
        long i = Double.doubleToRawLongBits(d0) & 4503599627370495L;

        return Double.longBitsToDouble(i | DoubleUtils.ONE_BITS);
    }

    static double bigToDouble(BigInteger biginteger) {
        BigInteger biginteger1 = biginteger.abs();
        int i = biginteger1.bitLength() - 1;

        if (i < 63) {
            return (double) biginteger.longValue();
        } else if (i > 1023) {
            return (double) biginteger.signum() * Double.POSITIVE_INFINITY;
        } else {
            int j = i - 52 - 1;
            long k = biginteger1.shiftRight(j).longValue();
            long l = k >> 1;

            l &= 4503599627370495L;
            boolean flag = (k & 1L) != 0L && ((l & 1L) != 0L || biginteger1.getLowestSetBit() < j);
            long i1 = flag ? l + 1L : l;
            long j1 = (long) (i + 1023) << 52;

            j1 += i1;
            j1 |= (long) biginteger.signum() & Long.MIN_VALUE;
            return Double.longBitsToDouble(j1);
        }
    }

    static double ensureNonNegative(double d0) {
        Preconditions.checkArgument(!Double.isNaN(d0));
        return d0 > 0.0D ? d0 : 0.0D;
    }
}
