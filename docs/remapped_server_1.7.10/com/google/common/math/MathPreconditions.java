package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import java.math.BigInteger;
import javax.annotation.Nullable;

@GwtCompatible
final class MathPreconditions {

    static int checkPositive(@Nullable String s, int i) {
        if (i <= 0) {
            throw new IllegalArgumentException(s + " (" + i + ") must be > 0");
        } else {
            return i;
        }
    }

    static long checkPositive(@Nullable String s, long i) {
        if (i <= 0L) {
            throw new IllegalArgumentException(s + " (" + i + ") must be > 0");
        } else {
            return i;
        }
    }

    static BigInteger checkPositive(@Nullable String s, BigInteger biginteger) {
        if (biginteger.signum() <= 0) {
            throw new IllegalArgumentException(s + " (" + biginteger + ") must be > 0");
        } else {
            return biginteger;
        }
    }

    static int checkNonNegative(@Nullable String s, int i) {
        if (i < 0) {
            throw new IllegalArgumentException(s + " (" + i + ") must be >= 0");
        } else {
            return i;
        }
    }

    static long checkNonNegative(@Nullable String s, long i) {
        if (i < 0L) {
            throw new IllegalArgumentException(s + " (" + i + ") must be >= 0");
        } else {
            return i;
        }
    }

    static BigInteger checkNonNegative(@Nullable String s, BigInteger biginteger) {
        if (biginteger.signum() < 0) {
            throw new IllegalArgumentException(s + " (" + biginteger + ") must be >= 0");
        } else {
            return biginteger;
        }
    }

    static double checkNonNegative(@Nullable String s, double d0) {
        if (d0 < 0.0D) {
            throw new IllegalArgumentException(s + " (" + d0 + ") must be >= 0");
        } else {
            return d0;
        }
    }

    static void checkRoundingUnnecessary(boolean flag) {
        if (!flag) {
            throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
        }
    }

    static void checkInRange(boolean flag) {
        if (!flag) {
            throw new ArithmeticException("not in range");
        }
    }

    static void checkNoOverflow(boolean flag) {
        if (!flag) {
            throw new ArithmeticException("overflow");
        }
    }

    private MathPreconditions() {}
}
