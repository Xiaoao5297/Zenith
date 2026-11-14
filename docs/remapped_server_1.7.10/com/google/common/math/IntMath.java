package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.math.RoundingMode;

@GwtCompatible(emulated = true)
public final class IntMath {

    @VisibleForTesting
    static final int MAX_POWER_OF_SQRT2_UNSIGNED = -1257966797;
    @VisibleForTesting
    static final byte[] maxLog10ForLeadingZeros = new byte[]{9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0, 0};
    @VisibleForTesting
    static final int[] powersOf10 = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};
    @VisibleForTesting
    static final int[] halfPowersOf10 = new int[]{3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, Integer.MAX_VALUE};
    @VisibleForTesting
    static final int FLOOR_SQRT_MAX_INT = 46340;
    private static final int[] factorials = new int[]{1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600};
    @VisibleForTesting
    static int[] biggestBinomials = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, 65536, 2345, 477, 193, 110, 75, 58, 49, 43, 39, 37, 35, 34, 34, 33};

    public static boolean isPowerOfTwo(int i) {
        return i > 0 & (i & i - 1) == 0;
    }

    @VisibleForTesting
    static int lessThanBranchFree(int i, int j) {
        return ~(~(i - j)) >>> 31;
    }

    public static int log2(int i, RoundingMode roundingmode) {
        MathPreconditions.checkPositive("x", i);
        switch (roundingmode) {
            case UNNECESSARY:
                MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(i));
            case DOWN:
            case FLOOR:
                return 31 - Integer.numberOfLeadingZeros(i);
            case UP:
            case CEILING:
                return 32 - Integer.numberOfLeadingZeros(i - 1);
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                int j = Integer.numberOfLeadingZeros(i);
                int k = -1257966797 >>> j;
                int l = 31 - j;

                return l + lessThanBranchFree(k, i);
            default:
                throw new AssertionError();
        }
    }

    @GwtIncompatible("need BigIntegerMath to adequately test")
    public static int log10(int i, RoundingMode roundingmode) {
        MathPreconditions.checkPositive("x", i);
        int j = log10Floor(i);
        int k = IntMath.powersOf10[j];

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
                return j + lessThanBranchFree(IntMath.halfPowersOf10[j], i);
            default:
                throw new AssertionError();
        }
    }

    private static int log10Floor(int i) {
        byte b0 = IntMath.maxLog10ForLeadingZeros[Integer.numberOfLeadingZeros(i)];

        return b0 - lessThanBranchFree(i, IntMath.powersOf10[b0]);
    }

    @GwtIncompatible("failing tests")
    public static int pow(int i, int j) {
        MathPreconditions.checkNonNegative("exponent", j);
        switch (i) {
            case -2:
                if (j < 32) {
                    return (j & 1) == 0 ? 1 << j : -(1 << j);
                }

                return 0;
            case -1:
                return (j & 1) == 0 ? 1 : -1;
            case 0:
                return j == 0 ? 1 : 0;
            case 1:
                return 1;
            case 2:
                return j < 32 ? 1 << j : 0;
            default:
                int k = 1;

                while (true) {
                    switch (j) {
                        case 0:
                            return k;
                        case 1:
                            return i * k;
                    }

                    k *= (j & 1) == 0 ? 1 : i;
                    i *= i;
                    j >>= 1;
                }
        }
    }

    @GwtIncompatible("need BigIntegerMath to adequately test")
    public static int sqrt(int i, RoundingMode roundingmode) {
        MathPreconditions.checkNonNegative("x", i);
        int j = sqrtFloor(i);

        switch (roundingmode) {
            case UNNECESSARY:
                MathPreconditions.checkRoundingUnnecessary(j * j == i);
            case DOWN:
            case FLOOR:
                return j;
            case UP:
            case CEILING:
                return j + lessThanBranchFree(j * j, i);
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                int k = j * j + j;

                return j + lessThanBranchFree(k, i);
            default:
                throw new AssertionError();
        }
    }

    private static int sqrtFloor(int i) {
        return (int) Math.sqrt((double) i);
    }

    public static int divide(int i, int j, RoundingMode roundingmode) {
        Preconditions.checkNotNull(roundingmode);
        if (j == 0) {
            throw new ArithmeticException("/ by zero");
        } else {
            int k = i / j;
            int l = i - j * k;

            if (l == 0) {
                return k;
            } else {
                int i1 = 1 | (i ^ j) >> 31;
                boolean flag;

                switch (roundingmode) {
                    case UNNECESSARY:
                        MathPreconditions.checkRoundingUnnecessary(l == 0);
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
                        int j1 = Math.abs(l);
                        int k1 = j1 - (Math.abs(j) - j1);

                        if (k1 == 0) {
                            flag = roundingmode == RoundingMode.HALF_UP || roundingmode == RoundingMode.HALF_EVEN & (k & 1) != 0;
                        } else {
                            flag = k1 > 0;
                        }
                        break;
                    default:
                        throw new AssertionError();
                }

                return flag ? k + i1 : k;
            }
        }
    }

    public static int mod(int i, int j) {
        if (j <= 0) {
            throw new ArithmeticException("Modulus " + j + " must be > 0");
        } else {
            int k = i % j;

            return k >= 0 ? k : k + j;
        }
    }

    public static int gcd(int i, int j) {
        MathPreconditions.checkNonNegative("a", i);
        MathPreconditions.checkNonNegative("b", j);
        if (i == 0) {
            return j;
        } else if (j == 0) {
            return i;
        } else {
            int k = Integer.numberOfTrailingZeros(i);

            i >>= k;
            int l = Integer.numberOfTrailingZeros(j);

            for (j >>= l; i != j; i >>= Integer.numberOfTrailingZeros(i)) {
                int i1 = i - j;
                int j1 = i1 & i1 >> 31;

                i = i1 - j1 - j1;
                j += j1;
            }

            return i << Math.min(k, l);
        }
    }

    public static int checkedAdd(int i, int j) {
        long k = (long) i + (long) j;

        MathPreconditions.checkNoOverflow(k == (long) ((int) k));
        return (int) k;
    }

    public static int checkedSubtract(int i, int j) {
        long k = (long) i - (long) j;

        MathPreconditions.checkNoOverflow(k == (long) ((int) k));
        return (int) k;
    }

    public static int checkedMultiply(int i, int j) {
        long k = (long) i * (long) j;

        MathPreconditions.checkNoOverflow(k == (long) ((int) k));
        return (int) k;
    }

    public static int checkedPow(int i, int j) {
        MathPreconditions.checkNonNegative("exponent", j);
        switch (i) {
            case -2:
                MathPreconditions.checkNoOverflow(j < 32);
                return (j & 1) == 0 ? 1 << j : -1 << j;
            case -1:
                return (j & 1) == 0 ? 1 : -1;
            case 0:
                return j == 0 ? 1 : 0;
            case 1:
                return 1;
            case 2:
                MathPreconditions.checkNoOverflow(j < 31);
                return 1 << j;
            default:
                int k = 1;

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
                        MathPreconditions.checkNoOverflow(-46340 <= i & i <= 46340);
                        i *= i;
                    }
                }
        }
    }

    public static int factorial(int i) {
        MathPreconditions.checkNonNegative("n", i);
        return i < IntMath.factorials.length ? IntMath.factorials[i] : Integer.MAX_VALUE;
    }

    @GwtIncompatible("need BigIntegerMath to adequately test")
    public static int binomial(int i, int j) {
        MathPreconditions.checkNonNegative("n", i);
        MathPreconditions.checkNonNegative("k", j);
        Preconditions.checkArgument(j <= i, "k (%s) > n (%s)", j, i);
        if (j > i >> 1) {
            j = i - j;
        }

        if (j < IntMath.biggestBinomials.length && i <= IntMath.biggestBinomials[j]) {
            switch (j) {
                case 0:
                    return 1;
                case 1:
                    return i;
                default:
                    long k = 1L;

                    for (int l = 0; l < j; ++l) {
                        k *= (long) (i - l);
                        k /= (long) (l + 1);
                    }

                    return (int) k;
            }
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public static int mean(int i, int j) {
        return (i & j) + ((i ^ j) >> 1);
    }

    private IntMath() {}
}
