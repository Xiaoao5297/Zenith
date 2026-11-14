package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.math.RoundingMode;

@GwtCompatible(emulated = true)
public final class LongMath {

    @VisibleForTesting
    static final long MAX_POWER_OF_SQRT2_UNSIGNED = -5402926248376769404L;
    @VisibleForTesting
    static final byte[] maxLog10ForLeadingZeros = new byte[]{19, 18, 18, 18, 18, 17, 17, 17, 16, 16, 16, 15, 15, 15, 15, 14, 14, 14, 13, 13, 13, 12, 12, 12, 12, 11, 11, 11, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0};
    @GwtIncompatible("TODO")
    @VisibleForTesting
    static final long[] powersOf10 = new long[]{1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L};
    @GwtIncompatible("TODO")
    @VisibleForTesting
    static final long[] halfPowersOf10 = new long[]{3L, 31L, 316L, 3162L, 31622L, 316227L, 3162277L, 31622776L, 316227766L, 3162277660L, 31622776601L, 316227766016L, 3162277660168L, 31622776601683L, 316227766016837L, 3162277660168379L, 31622776601683793L, 316227766016837933L, 3162277660168379331L};
    @VisibleForTesting
    static final long FLOOR_SQRT_MAX_LONG = 3037000499L;
    static final long[] factorials = new long[]{1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L};
    static final int[] biggestBinomials = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3810779, 121977, 16175, 4337, 1733, 887, 534, 361, 265, 206, 169, 143, 125, 111, 101, 94, 88, 83, 79, 76, 74, 72, 70, 69, 68, 67, 67, 66, 66, 66, 66};
    @VisibleForTesting
    static final int[] biggestSimpleBinomials = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2642246, 86251, 11724, 3218, 1313, 684, 419, 287, 214, 169, 139, 119, 105, 95, 87, 81, 76, 73, 70, 68, 66, 64, 63, 62, 62, 61, 61, 61};

    public static boolean isPowerOfTwo(long i) {
        return i > 0L & (i & i - 1L) == 0L;
    }

    @VisibleForTesting
    static int lessThanBranchFree(long i, long j) {
        return (int) (~(~(i - j)) >>> 63);
    }

    public static int log2(long i, RoundingMode roundingmode) {
        MathPreconditions.checkPositive("x", i);
        switch (roundingmode) {
            case UNNECESSARY:
                MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(i));
            case DOWN:
            case FLOOR:
                return 63 - Long.numberOfLeadingZeros(i);
            case UP:
            case CEILING:
                return 64 - Long.numberOfLeadingZeros(i - 1L);
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                int j = Long.numberOfLeadingZeros(i);
                long k = -5402926248376769404L >>> j;
                int l = 63 - j;

                return l + lessThanBranchFree(k, i);
            default:
                throw new AssertionError("impossible");
        }
    }

    @GwtIncompatible("TODO")
    public static int log10(long i, RoundingMode roundingmode) {
        MathPreconditions.checkPositive("x", i);
        int j = log10Floor(i);
        long k = LongMath.powersOf10[j];

        switch (roundingmode) {
            case UNNECESSARY:
                MathPreconditions.checkRoundingUnnecessary(i == k);
            case DOWN:
            case FLOOR:
                return j;
            case UP:
            case CEILING:
                return j + lessThanBranchFree(k, i);
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                return j + lessThanBranchFree(LongMath.halfPowersOf10[j], i);
            default:
                throw new AssertionError();
        }
    }

    @GwtIncompatible("TODO")
    static int log10Floor(long i) {
        byte b0 = LongMath.maxLog10ForLeadingZeros[Long.numberOfLeadingZeros(i)];

        return b0 - lessThanBranchFree(i, LongMath.powersOf10[b0]);
    }

    @GwtIncompatible("TODO")
    public static long pow(long i, int j) {
        MathPreconditions.checkNonNegative("exponent", j);
        if (-2L <= i && i <= 2L) {
            switch ((int) i) {
                case -2:
                    if (j < 64) {
                        return (j & 1) == 0 ? 1L << j : -(1L << j);
                    }

                    return 0L;
                case -1:
                    return (j & 1) == 0 ? 1L : -1L;
                case 0:
                    return j == 0 ? 1L : 0L;
                case 1:
                    return 1L;
                case 2:
                    return j < 64 ? 1L << j : 0L;
                default:
                    throw new AssertionError();
            }
        } else {
            long k = 1L;

            while (true) {
                switch (j) {
                    case 0:
                        return k;
                    case 1:
                        return k * i;
                }

                k *= (j & 1) == 0 ? 1L : i;
                i *= i;
                j >>= 1;
            }
        }
    }

    @GwtIncompatible("TODO")
    public static long sqrt(long i, RoundingMode roundingmode) {
        MathPreconditions.checkNonNegative("x", i);
        if (fitsInInt(i)) {
            return (long) IntMath.sqrt((int) i, roundingmode);
        } else {
            long j = (long) Math.sqrt((double) i);
            long k = j * j;

            switch (roundingmode) {
                case UNNECESSARY:
                    MathPreconditions.checkRoundingUnnecessary(k == i);
                    return j;
                case DOWN:
                case FLOOR:
                    if (i < k) {
                        return j - 1L;
                    }

                    return j;
                case UP:
                case CEILING:
                    if (i > k) {
                        return j + 1L;
                    }

                    return j;
                case HALF_DOWN:
                case HALF_UP:
                case HALF_EVEN:
                    long l = j - (long) (i < k ? 1 : 0);
                    long i1 = l * l + l;

                    return l + (long) lessThanBranchFree(i1, i);
                default:
                    throw new AssertionError();
            }
        }
    }

    @GwtIncompatible("TODO")
    public static long divide(long i, long j, RoundingMode roundingmode) {
        Preconditions.checkNotNull(roundingmode);
        long k = i / j;
        long l = i - j * k;

        if (l == 0L) {
            return k;
        } else {
            int i1 = 1 | (int) ((i ^ j) >> 63);
            boolean flag;

            switch (roundingmode) {
                case UNNECESSARY:
                    MathPreconditions.checkRoundingUnnecessary(l == 0L);
                case DOWN:
                    flag = false;
                    break;
                case FLOOR:
                    flag = i1 < 0;
                    break;
                case UP:
                    flag = true;
                    break;
                case CEILING:
                    flag = i1 > 0;
                    break;
                case HALF_DOWN:
                case HALF_UP:
                case HALF_EVEN:
                    long j1 = Math.abs(l);
                    long k1 = j1 - (Math.abs(j) - j1);

                    if (k1 == 0L) {
                        flag = roundingmode == RoundingMode.HALF_UP | roundingmode == RoundingMode.HALF_EVEN & (k & 1L) != 0L;
                    } else {
                        flag = k1 > 0L;
                    }
                    break;
                default:
                    throw new AssertionError();
            }

            return flag ? k + (long) i1 : k;
        }
    }

    @GwtIncompatible("TODO")
    public static int mod(long i, int j) {
        return (int) mod(i, (long) j);
    }

    @GwtIncompatible("TODO")
    public static long mod(long i, long j) {
        if (j <= 0L) {
            throw new ArithmeticException("Modulus must be positive");
        } else {
            long k = i % j;

            return k >= 0L ? k : k + j;
        }
    }

    public static long gcd(long i, long j) {
        MathPreconditions.checkNonNegative("a", i);
        MathPreconditions.checkNonNegative("b", j);
        if (i == 0L) {
            return j;
        } else if (j == 0L) {
            return i;
        } else {
            int k = Long.numberOfTrailingZeros(i);

            i >>= k;
            int l = Long.numberOfTrailingZeros(j);

            for (j >>= l; i != j; i >>= Long.numberOfTrailingZeros(i)) {
                long i1 = i - j;
                long j1 = i1 & i1 >> 63;

                i = i1 - j1 - j1;
                j += j1;
            }

            return i << Math.min(k, l);
        }
    }

    @GwtIncompatible("TODO")
    public static long checkedAdd(long i, long j) {
        long k = i + j;

        MathPreconditions.checkNoOverflow((i ^ j) < 0L | (i ^ k) >= 0L);
        return k;
    }

    @GwtIncompatible("TODO")
    public static long checkedSubtract(long i, long j) {
        long k = i - j;

        MathPreconditions.checkNoOverflow((i ^ j) >= 0L | (i ^ k) >= 0L);
        return k;
    }

    @GwtIncompatible("TODO")
    public static long checkedMultiply(long i, long j) {
        int k = Long.numberOfLeadingZeros(i) + Long.numberOfLeadingZeros(~i) + Long.numberOfLeadingZeros(j) + Long.numberOfLeadingZeros(~j);

        if (k > 65) {
            return i * j;
        } else {
            MathPreconditions.checkNoOverflow(k >= 64);
            MathPreconditions.checkNoOverflow(i >= 0L | j != Long.MIN_VALUE);
            long l = i * j;

            MathPreconditions.checkNoOverflow(i == 0L || l / i == j);
            return l;
        }
    }

    @GwtIncompatible("TODO")
    public static long checkedPow(long i, int j) {
        MathPreconditions.checkNonNegative("exponent", j);
        if (i >= -2L & i <= 2L) {
            switch ((int) i) {
                case -2:
                    MathPreconditions.checkNoOverflow(j < 64);
                    return (j & 1) == 0 ? 1L << j : -1L << j;
                case -1:
                    return (j & 1) == 0 ? 1L : -1L;
                case 0:
                    return j == 0 ? 1L : 0L;
                case 1:
                    return 1L;
                case 2:
                    MathPreconditions.checkNoOverflow(j < 63);
                    return 1L << j;
                default:
                    throw new AssertionError();
            }
        } else {
            long k = 1L;

            while (true) {
                switch (j) {
                    case 0:
                        return k;
                    case 1:
                        return checkedMultiply(k, i);
                }

                if ((j & 1) != 0) {
                    k = checkedMultiply(k, i);
                }

                j >>= 1;
                if (j > 0) {
                    MathPreconditions.checkNoOverflow(i <= 3037000499L);
                    i *= i;
                }
            }
        }
    }

    @GwtIncompatible("TODO")
    public static long factorial(int i) {
        MathPreconditions.checkNonNegative("n", i);
        return i < LongMath.factorials.length ? LongMath.factorials[i] : Long.MAX_VALUE;
    }

    public static long binomial(int i, int j) {
        MathPreconditions.checkNonNegative("n", i);
        MathPreconditions.checkNonNegative("k", j);
        Preconditions.checkArgument(j <= i, "k (%s) > n (%s)", j, i);
        if (j > i >> 1) {
            j = i - j;
        }

        switch (j) {
            case 0:
                return 1L;
            case 1:
                return (long) i;
            default:
                if (i < LongMath.factorials.length) {
                    return LongMath.factorials[i] / (LongMath.factorials[j] * LongMath.factorials[i - j]);
                } else if (j < LongMath.biggestBinomials.length && i <= LongMath.biggestBinomials[j]) {
                    if (j < LongMath.biggestSimpleBinomials.length && i <= LongMath.biggestSimpleBinomials[j]) {
                        long k = (long) (i--);

                        for (int l = 2; l <= j; ++l) {
                            k *= (long) i;
                            k /= (long) l;
                            --i;
                        }

                        return k;
                    } else {
                        int i1 = log2((long) i, RoundingMode.CEILING);
                        long j1 = 1L;
                        long k1 = (long) (i--);
                        long l1 = 1L;
                        int i2 = i1;

                        for (int j2 = 2; j2 <= j; --i) {
                            if (i2 + i1 < 63) {
                                k1 *= (long) i;
                                l1 *= (long) j2;
                                i2 += i1;
                            } else {
                                j1 = multiplyFraction(j1, k1, l1);
                                k1 = (long) i;
                                l1 = (long) j2;
                                i2 = i1;
                            }

                            ++j2;
                        }

                        return multiplyFraction(j1, k1, l1);
                    }
                } else {
                    return Long.MAX_VALUE;
                }
        }
    }

    static long multiplyFraction(long i, long j, long k) {
        if (i == 1L) {
            return j / k;
        } else {
            long l = gcd(i, k);

            i /= l;
            k /= l;
            return i * (j / k);
        }
    }

    static boolean fitsInInt(long i) {
        return (long) ((int) i) == i;
    }

    public static long mean(long i, long j) {
        return (i & j) + ((i ^ j) >> 1);
    }

    private LongMath() {}
}
